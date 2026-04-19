package com.openimgs.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class PremiumFeature {
    UNLIMITED_ALBUMS,
    UNLIMITED_SEARCH,
    UNLIMITED_DUPLICATE_DELETE,
    BATCH_CLEANUP
}
