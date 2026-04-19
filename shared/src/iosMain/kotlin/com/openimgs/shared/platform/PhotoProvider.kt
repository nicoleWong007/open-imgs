package com.openimgs.shared.platform

import com.openimgs.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Clock

actual class PhotoProvider actual constructor() {

    actual suspend fun requestPermission(): Boolean {
        return false
    }

    actual fun hasPermission(): Boolean {
        return false
    }

    actual fun observeChanges(): Flow<Unit> = emptyFlow()

    actual suspend fun loadPhotos(limit: Int, offset: Int): List<Photo> {
        return emptyList()
    }

    actual suspend fun getPhotoById(id: String): Photo? {
        return null
    }

    actual suspend fun deletePhotos(ids: List<String>): List<String> {
        return emptyList()
    }

    actual suspend fun getPhotoCount(): Int {
        return 0
    }

    actual suspend fun getThumbnail(id: String, width: Int, height: Int): ByteArray? {
        return null
    }

    actual suspend fun getFullImage(id: String): ByteArray? {
        return null
    }
}
