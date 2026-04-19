package com.openimgs.shared.domain.repository

import com.openimgs.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun observePhotos(): Flow<List<Photo>>
    suspend fun getPhotosPage(page: Int, pageSize: Int): List<Photo>
    suspend fun getPhotoById(id: String): Photo?
    suspend fun getPhotosByDateRange(startMs: Long, endMs: Long): List<Photo>
    suspend fun getFavorites(): List<Photo>
    suspend fun insertPhoto(photo: Photo)
    suspend fun insertPhotos(photos: List<Photo>)
    suspend fun deletePhoto(id: String)
    suspend fun deletePhotos(ids: List<String>)
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
    suspend fun getPhotoCount(): Int
    suspend fun getTotalSize(): Long
    suspend fun getUnindexedPhotoIds(limit: Int): List<String>
    suspend fun updateHashes(id: String, phash: String, dhash: String)
    suspend fun updateEmbedding(id: String, embedding: ByteArray)
}
