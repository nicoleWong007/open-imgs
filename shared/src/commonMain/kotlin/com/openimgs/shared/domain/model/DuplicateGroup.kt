package com.openimgs.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DuplicateGroup(
    val id: String,
    val originalId: String,
    val duplicateIds: List<String>,
    val similarity: Float,
    val status: DuplicateStatus = DuplicateStatus.PENDING
)
