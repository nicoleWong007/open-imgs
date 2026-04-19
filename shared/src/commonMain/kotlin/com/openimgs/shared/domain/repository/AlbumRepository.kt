package com.openimgs.shared.domain.repository

import com.openimgs.shared.domain.model.Album
import com.openimgs.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun observeAlbums(): Flow<List<Album>>
    suspend fun getAllAlbums(): List<Album>
    suspend fun getAlbumById(id: String): Album?
    suspend fun createAlbum(album: Album)
    suspend fun updateAlbumName(id: String, name: String, updatedAt: Long)
    suspend fun updateAlbumCover(id: String, coverPhotoId: String?, updatedAt: Long)
    suspend fun deleteAlbum(id: String)
    suspend fun addPhotoToAlbum(albumId: String, photoId: String, addedAt: Long)
    suspend fun removePhotoFromAlbum(albumId: String, photoId: String)
    suspend fun getPhotosByAlbum(albumId: String): List<Photo>
    suspend fun getPhotoCountForAlbum(albumId: String): Int
    suspend fun getAlbumCount(): Int
    suspend fun isPhotoInAlbum(albumId: String, photoId: String): Boolean
}
