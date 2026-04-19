package com.openimgs.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StorageCategory(
    val type: StorageType,
    val itemCount: Int,
    val totalSize: Long,
    val displayName: String
)
