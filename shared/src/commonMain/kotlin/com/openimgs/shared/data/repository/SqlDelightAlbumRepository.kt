package com.openimgs.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.openimgs.shared.data.database.DatabaseHelper
import com.openimgs.shared.domain.model.Album
import com.openimgs.shared.domain.model.Photo
import com.openimgs.shared.domain.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class SqlDelightAlbumRepository(
    private val db: DatabaseHelper
) : AlbumRepository {

    override fun observeAlbums(): Flow<List<Album>> {
        return db.albumQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toAlbum() } }
    }

    override suspend fun getAllAlbums(): List<Album> {
        return db.albumQueries.selectAll()
            .executeAsList()
            .map { it.toAlbum() }
    }

    override suspend fun getAlbumById(id: String): Album? {
        return db.albumQueries.selectById(id)
            .executeAsOneOrNull()
            ?.toAlbum()
    }

    override suspend fun createAlbum(album: Album) {
        db.albumQueries.insert(
            id = album.id,
            name = album.name,
            cover_photo_id = album.coverPhotoId,
            created_at = album.createdAt.toEpochMilliseconds(),
            updated_at = album.updatedAt.toEpochMilliseconds()
        )
    }

    override suspend fun updateAlbumName(id: String, name: String, updatedAt: Long) {
        db.albumQueries.updateName(name, updatedAt, id)
    }

    override suspend fun updateAlbumCover(id: String, coverPhotoId: String?, updatedAt: Long) {
        db.albumQueries.updateCover(coverPhotoId, updatedAt, id)
    }

    override suspend fun deleteAlbum(id: String) {
        db.albumQueries.deleteById(id)
    }

    override suspend fun addPhotoToAlbum(albumId: String, photoId: String, addedAt: Long) {
        db.albumQueries.addPhoto(albumId, photoId, addedAt)
    }

    override suspend fun removePhotoFromAlbum(albumId: String, photoId: String) {
        db.albumQueries.removePhoto(albumId, photoId)
    }

    override suspend fun getPhotosByAlbum(albumId: String): List<Photo> {
        return db.albumQueries.selectPhotosByAlbum(albumId)
            .executeAsList()
            .map { it.toPhoto() }
    }

    override suspend fun getPhotoCountForAlbum(albumId: String): Int {
        return db.albumQueries.photoCount(albumId)
            .executeAsOne()
            .toInt()
    }

    override suspend fun getAlbumCount(): Int {
        return db.albumQueries.albumCount()
            .executeAsOne()
            .toInt()
    }

    override suspend fun isPhotoInAlbum(albumId: String, photoId: String): Boolean {
        return db.albumQueries.isPhotoInAlbum(albumId, photoId)
            .executeAsOne() > 0
    }
}

private fun com.openimgs.shared.database.Albums.toAlbum(): Album {
    return Album(
        id = id,
        name = name,
        coverPhotoId = cover_photo_id,
        createdAt = Instant.fromEpochMilliseconds(created_at),
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
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
