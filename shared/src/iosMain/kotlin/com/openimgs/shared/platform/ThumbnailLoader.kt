package com.openimgs.shared.platform

actual class ThumbnailLoader actual constructor() {
    actual suspend fun loadThumbnail(photoId: String, width: Int, height: Int): ByteArray? {
        return null
    }
}
