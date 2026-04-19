package com.openimgs.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val query: String,
    val photoIds: List<String>,
    val confidence: Float,
    val timestamp: Instant
)
