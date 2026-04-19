package com.openimgs.shared.data.search

interface EmbeddingProvider {
    suspend fun encode(imageData: ByteArray): FloatArray
    suspend fun encodeBatch(images: List<ByteArray>): List<FloatArray>
    val embeddingDimension: Int
}
