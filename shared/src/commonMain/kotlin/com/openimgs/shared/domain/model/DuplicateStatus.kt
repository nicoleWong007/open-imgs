package com.openimgs.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DuplicateStatus {
    PENDING,
    REVIEWED,
    RESOLVED
}
