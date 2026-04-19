package com.openimgs.shared.platform

expect class ThumbnailLoader() {
    suspend fun loadThumbnail(photoId: String, width: Int, height: Int): ByteArray?
}
