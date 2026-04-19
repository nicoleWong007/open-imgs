package com.openimgs.shared.data.search

import com.openimgs.shared.domain.repository.PhotoRepository

class SearchEngine(
    private val embeddingProvider: EmbeddingProvider,
    private val photoRepository: PhotoRepository
) {

    data class SearchResult(
        val photoId: String,
        val score: Float
    )

    suspend fun indexPhoto(photoId: String, imageData: ByteArray) {
        val embedding = embeddingProvider.encode(imageData)
        photoRepository.updateEmbedding(photoId, embeddingToBytes(embedding))
    }

    suspend fun searchByText(query: String, limit: Int = 50): List<SearchResult> {
        return photoRepository.getPhotosByDateRange(0, Long.MAX_VALUE)
            .filter { it.path.contains(query, ignoreCase = true) }
            .take(limit)
            .map { SearchResult(it.id, 1.0f) }
    }

    suspend fun searchByImage(queryImageData: ByteArray, limit: Int = 20): List<SearchResult> {
        val queryEmbedding = embeddingProvider.encode(queryImageData)
        val queryVector = queryEmbedding
        // Vector similarity search would compare against stored embeddings
        return emptyList()
    }

    suspend fun searchBySimilarity(
        targetPhotoId: String,
        limit: Int = 20
    ): List<SearchResult> {
        val targetPhoto = photoRepository.getPhotoById(targetPhotoId) ?: return emptyList()
        // Would retrieve target embedding and compare against all indexed photos
        return emptyList()
    }

    private fun embeddingToBytes(embedding: FloatArray): ByteArray {
        val bytes = ByteArray(embedding.size * 4)
        for (i in embedding.indices) {
            val bits = embedding[i].toRawBits()
            bytes[i * 4] = (bits shr 24).toByte()
            bytes[i * 4 + 1] = (bits shr 16).toByte()
            bytes[i * 4 + 2] = (bits shr 8).toByte()
            bytes[i * 4 + 3] = bits.toByte()
        }
        return bytes
    }

    private fun bytesToFloatArray(bytes: ByteArray): FloatArray {
        val floats = FloatArray(bytes.size / 4)
        for (i in floats.indices) {
            val bits = ((bytes[i * 4].toInt() and 0xFF) shl 24) or
                ((bytes[i * 4 + 1].toInt() and 0xFF) shl 16) or
                ((bytes[i * 4 + 2].toInt() and 0xFF) shl 8) or
                (bytes[i * 4 + 3].toInt() and 0xFF)
            floats[i] = Float.fromBits(bits)
        }
        return floats
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB)
        return if (denominator > 0f) dotProduct / denominator else 0f
    }
}
