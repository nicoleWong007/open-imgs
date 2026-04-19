package com.openimgs.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.openimgs.shared.data.database.DatabaseHelper
import com.openimgs.shared.domain.model.Photo
import com.openimgs.shared.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class SqlDelightPhotoRepository(
    private val db: DatabaseHelper
) : PhotoRepository {

    override fun observePhotos(): Flow<List<Photo>> {
        return db.photoQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toPhoto() } }
    }

    override suspend fun getPhotosPage(page: Int, pageSize: Int): List<Photo> {
        val offset = page * pageSize
        return db.photoQueries.selectPaginated(pageSize.toLong(), offset.toLong())
            .executeAsList()
            .map { it.toPhoto() }
    }

    override suspend fun getPhotoById(id: String): Photo? {
        return db.photoQueries.selectById(id)
            .executeAsOneOrNull()
            ?.toPhoto()
    }

    override suspend fun getPhotosByDateRange(startMs: Long, endMs: Long): List<Photo> {
        return db.photoQueries.selectByDateRange(startMs, endMs)
            .executeAsList()
            .map { it.toPhoto() }
    }

    override suspend fun getFavorites(): List<Photo> {
        return db.photoQueries.selectFavorites()
            .executeAsList()
            .map { it.toPhoto() }
    }

    override suspend fun insertPhoto(photo: Photo) {
        db.photoQueries.insert(
            id = photo.id,
            path = photo.path,
            date_taken = photo.dateTaken.toEpochMilliseconds(),
            size = photo.size,
            mime_type = photo.mimeType,
            width = photo.width.toLong(),
            height = photo.height.toLong(),
            orientation = photo.orientation.toLong(),
            is_favorite = if (photo.isFavorite) 1 else 0
        )
    }

    override suspend fun insertPhotos(photos: List<Photo>) {
        db.photoQueries.transaction {
            photos.forEach { photo ->
                db.photoQueries.insert(
                    id = photo.id,
                    path = photo.path,
                    date_taken = photo.dateTaken.toEpochMilliseconds(),
                    size = photo.size,
                    mime_type = photo.mimeType,
                    width = photo.width.toLong(),
                    height = photo.height.toLong(),
                    orientation = photo.orientation.toLong(),
                    is_favorite = if (photo.isFavorite) 1 else 0
                )
            }
        }
    }

    override suspend fun deletePhoto(id: String) {
        db.photoQueries.deleteById(id)
    }

    override suspend fun deletePhotos(ids: List<String>) {
        db.photoQueries.transaction {
            ids.forEach { id ->
                db.photoQueries.deleteById(id)
            }
        }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        db.photoQueries.updateFavorite(if (isFavorite) 1 else 0, id)
    }

    override suspend fun getPhotoCount(): Int {
        return db.photoQueries.count().executeAsOne().toInt()
    }

    override suspend fun getTotalSize(): Long {
        return db.photoQueries.totalSize().executeAsOne().SUM ?: 0L
    }

    override suspend fun getUnindexedPhotoIds(limit: Int): List<String> {
        return db.photoQueries.selectUnindexed(limit.toLong())
            .executeAsList()
            .map { it.id }
    }

    override suspend fun updateHashes(id: String, phash: String, dhash: String) {
        db.photoQueries.updateHashes(phash, dhash, id)
    }

    override suspend fun updateEmbedding(id: String, embedding: ByteArray) {
        db.photoQueries.updateEmbedding(embedding, id)
    }
}

private fun com.openimgs.shared.database.Photos.toPhoto(): Photo {
    return Photo(
        id = id,
        path = path,
        dateTaken = Instant.fromEpochMilliseconds(date_taken),
        size = size,
        mimeType = mime_type,
        width = width.toInt(),
        height = height.toInt(),
        orientation = orientation.toInt(),
        isFavorite = is_favorite == 1L
    )
}
