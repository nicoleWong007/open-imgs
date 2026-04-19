package com.openimgs.shared.data.storage

import com.openimgs.shared.domain.model.Photo
import com.openimgs.shared.domain.model.StorageCategory
import com.openimgs.shared.domain.model.StorageType
import com.openimgs.shared.domain.repository.PhotoRepository

class StorageAnalyzer(
    private val photoRepository: PhotoRepository
) {

    data class StorageReport(
        val totalPhotos: Int,
        val totalSize: Long,
        val categories: List<StorageCategory>,
        val potentialSavings: Long
    )

    suspend fun analyze(): StorageReport {
        val allPhotos = photoRepository.getPhotosByDateRange(0, Long.MAX_VALUE)
        val categories = categorizePhotos(allPhotos)
        val potentialSavings = categories
            .filter { it.type != StorageType.OTHER }
            .sumOf { it.totalSize }

        return StorageReport(
            totalPhotos = photoRepository.getPhotoCount(),
            totalSize = photoRepository.getTotalSize(),
            categories = categories,
            potentialSavings = potentialSavings
        )
    }

    private fun categorizePhotos(photos: List<Photo>): List<StorageCategory> {
        val buckets = mutableMapOf<StorageType, MutableList<Photo>>(
            StorageType.SCREENSHOTS to mutableListOf(),
            StorageType.LARGE_VIDEOS to mutableListOf(),
            StorageType.SIMILAR_BURSTS to mutableListOf(),
            StorageType.BLURRED to mutableListOf(),
            StorageType.OTHER to mutableListOf()
        )

        for (photo in photos) {
            val type = classifyPhoto(photo)
            buckets.getOrPut(type) { mutableListOf() }.add(photo)
        }

        return buckets.map { (type, photos) ->
            StorageCategory(
                type = type,
                itemCount = photos.size,
                totalSize = photos.sumOf { it.size },
                displayName = type.displayName
            )
        }.filter { it.itemCount > 0 }
    }

    private fun classifyPhoto(photo: Photo): StorageType {
        if (isScreenshot(photo)) return StorageType.SCREENSHOTS
        if (isLargeVideo(photo)) return StorageType.LARGE_VIDEOS
        return StorageType.OTHER
    }

    private fun isScreenshot(photo: Photo): Boolean {
        return photo.path.contains("screenshot", ignoreCase = true) ||
            photo.path.contains("Screenshot", ignoreCase = true) ||
            photo.path.contains("ScreenShot", ignoreCase = true)
    }

    private fun isLargeVideo(photo: Photo): Boolean {
        return photo.size > LARGE_VIDEO_THRESHOLD && photo.mimeType.startsWith("video/")
    }

    companion object {
        private const val LARGE_VIDEO_THRESHOLD = 100L * 1024 * 1024 // 100MB
    }
}

private val StorageType.displayName: String
    get() = when (this) {
        StorageType.SCREENSHOTS -> "Screenshots"
        StorageType.DUPLICATES -> "Duplicates"
        StorageType.LARGE_VIDEOS -> "Large Videos"
        StorageType.SIMILAR_BURSTS -> "Similar Bursts"
        StorageType.BLURRED -> "Blurred Photos"
        StorageType.OTHER -> "Other"
    }
