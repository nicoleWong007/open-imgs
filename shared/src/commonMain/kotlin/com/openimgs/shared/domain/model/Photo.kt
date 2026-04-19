package com.openimgs.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: String,
    val path: String,
    val dateTaken: Instant,
    val size: Long,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val orientation: Int = 1,
    val isFavorite: Boolean = false,
    val albumIds: List<String> = emptyList()
)
