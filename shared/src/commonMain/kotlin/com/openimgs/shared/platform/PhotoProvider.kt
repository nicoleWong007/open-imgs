package com.openimgs.shared.platform

import com.openimgs.shared.domain.model.Photo
import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific photo provider.
 * iOS: PhotoKit (PHAsset)
 * Android: MediaStore (ContentResolver)
 */
expect class PhotoProvider() {
    /**
     * Request photo library permissions from the user.
     * @return true if granted, false if denied
     */
    suspend fun requestPermission(): Boolean

    /**
     * Check if photo library permissions are currently granted.
     */
    fun hasPermission(): Boolean

    /**
     * Observe the photo library for changes (new photos, deletions, etc.).
     * Emits Unit on each change.
     */
    fun observeChanges(): Flow<Unit>

    /**
     * Load photos paginated by date taken (newest first).
     * @param limit number of photos per page
     * @param offset number of photos to skip
     * @return list of photos for this page
     */
    suspend fun loadPhotos(limit: Int, offset: Int): List<Photo>

    /**
     * Load a single photo by its platform-specific identifier.
     */
    suspend fun getPhotoById(id: String): Photo?

    /**
     * Delete photos by their identifiers.
     * @return list of successfully deleted photo IDs
     */
    suspend fun deletePhotos(ids: List<String>): List<String>

    /**
     * Get total photo count in the library.
     */
    suspend fun getPhotoCount(): Int

    /**
     * Get the thumbnail data for a photo at the requested size.
     * @param id photo identifier
     * @param width requested thumbnail width in pixels
     * @param height requested thumbnail height in pixels
     * @return thumbnail byte data, or null if not available
     */
    suspend fun getThumbnail(id: String, width: Int, height: Int): ByteArray?

    /**
     * Get the full-size image data for a photo.
     * @param id photo identifier
     * @return full image byte data, or null if not available
     */
    suspend fun getFullImage(id: String): ByteArray?
}
