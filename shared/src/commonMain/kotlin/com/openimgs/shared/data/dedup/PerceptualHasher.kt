package com.openimgs.shared.data.dedup

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class PerceptualHasher : ImageHasher {

    override fun computePHash(pixels: IntArray, width: Int, height: Int): Long {
        val gray = toGrayscale(pixels)
        val resized = bilinearResize(gray, width, height, PHASH_SIZE)
        val dct = computeDCT(resized)
        val dctLow = extractLowFrequency(dct)
        val median = computeMedian(dctLow)
        return encodeHash(dctLow, median)
    }

    override fun computeDHash(pixels: IntArray, width: Int, height: Int): Long {
        val gray = toGrayscale(pixels)
        val resized = bilinearResize(gray, width, height, DHASH_SIZE + 1)
        var hash = 0L
        for (row in 0 until DHASH_SIZE) {
            for (col in 0 until DHASH_SIZE) {
                val left = resized[row * (DHASH_SIZE + 1) + col]
                val right = resized[row * (DHASH_SIZE + 1) + col + 1]
                hash = (hash shl 1) or (if (left > right) 1L else 0L)
            }
        }
        return hash
    }

    override fun hammingDistance(hash1: Long, hash2: Long): Int {
        return (hash1 xor hash2).countOneBits()
    }

    override fun similarity(hash1: Long, hash2: Long): Float {
        val distance = hammingDistance(hash1, hash2)
        return 1f - distance.toFloat() / Long.SIZE_BITS
    }

    private fun toGrayscale(pixels: IntArray): FloatArray {
        val gray = FloatArray(pixels.size)
        for (i in pixels.indices) {
            val r = (pixels[i] shr 16) and 0xFF
            val g = (pixels[i] shr 8) and 0xFF
            val b = pixels[i] and 0xFF
            gray[i] = 0.299f * r + 0.587f * g + 0.114f * b
        }
        return gray
    }

    private fun bilinearResize(
        src: FloatArray,
        srcWidth: Int,
        srcHeight: Int,
        dstSize: Int
    ): FloatArray {
        val dst = FloatArray(dstSize * dstSize)
        val xRatio = srcWidth.toFloat() / dstSize
        val yRatio = srcHeight.toFloat() / dstSize

        for (y in 0 until dstSize) {
            for (x in 0 until dstSize) {
                val srcX = x * xRatio
                val srcY = y * yRatio
                val x0 = srcX.toInt().coerceIn(0, srcWidth - 1)
                val y0 = srcY.toInt().coerceIn(0, srcHeight - 1)
                val x1 = (x0 + 1).coerceIn(0, srcWidth - 1)
                val y1 = (y0 + 1).coerceIn(0, srcHeight - 1)
                val xf = srcX - x0
                val yf = srcY - y0

                val top = src[y0 * srcWidth + x0] * (1 - xf) + src[y0 * srcWidth + x1] * xf
                val bottom = src[y1 * srcWidth + x0] * (1 - xf) + src[y1 * srcWidth + x1] * xf
                dst[y * dstSize + x] = top * (1 - yf) + bottom * yf
            }
        }
        return dst
    }

    private fun computeDCT(matrix: FloatArray): FloatArray {
        val n = PHASH_SIZE
        val result = FloatArray(n * n)
        val c = FloatArray(n)
        c[0] = 1f / sqrt(n.toFloat())
        for (i in 1 until n) {
            c[i] = sqrt(2f / n)
        }

        for (u in 0 until n) {
            for (v in 0 until n) {
                var sum = 0f
                for (i in 0 until n) {
                    for (j in 0 until n) {
                        sum += matrix[i * n + j] *
                            kotlin.math.cos((2 * i + 1) * u * PI / (2 * n)) *
                            kotlin.math.cos((2 * j + 1) * v * PI / (2 * n))
                    }
                }
                result[u * n + v] = c[u] * c[v] * sum
            }
        }
        return result
    }

    private fun extractLowFrequency(dct: FloatArray): FloatArray {
        val low = FloatArray(PHASH_LOW_SIZE * PHASH_LOW_SIZE)
        for (i in 0 until PHASH_LOW_SIZE) {
            for (j in 0 until PHASH_LOW_SIZE) {
                low[i * PHASH_LOW_SIZE + j] = dct[i * PHASH_SIZE + j]
            }
        }
        return low
    }

    private fun computeMedian(values: FloatArray): Float {
        val sorted = values.sorted().toFloatArray()
        val mid = sorted.size / 2
        return if (sorted.size % 2 == 0) {
            (sorted[mid - 1] + sorted[mid]) / 2f
        } else {
            sorted[mid]
        }
    }

    private fun encodeHash(values: FloatArray, median: Float): Long {
        var hash = 0L
        for (value in values) {
            hash = (hash shl 1) or (if (value > median) 1L else 0L)
        }
        return hash
    }

    companion object {
        private const val PI = 3.14159265358979323846f
        private const val PHASH_SIZE = 32
        private const val PHASH_LOW_SIZE = 8
        private const val DHASH_SIZE = 8
    }
}
