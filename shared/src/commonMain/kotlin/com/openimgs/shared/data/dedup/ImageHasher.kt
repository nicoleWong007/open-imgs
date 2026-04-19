package com.openimgs.shared.data.dedup

interface ImageHasher {
    fun computePHash(pixels: IntArray, width: Int, height: Int): Long
    fun computeDHash(pixels: IntArray, width: Int, height: Int): Long
    fun hammingDistance(hash1: Long, hash2: Long): Int
    fun similarity(hash1: Long, hash2: Long): Float
}
