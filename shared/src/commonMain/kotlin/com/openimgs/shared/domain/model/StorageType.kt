package com.openimgs.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class StorageType {
    SCREENSHOTS,
    DUPLICATES,
    LARGE_VIDEOS,
    SIMILAR_BURSTS,
    BLURRED,
    OTHER
}
