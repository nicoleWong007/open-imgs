package com.openimgs.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PremiumStatus(
    val isPremium: Boolean = false,
    val expiryDate: Instant? = null,
    val features: Set<PremiumFeature> = emptySet(),
    val searchesUsed: Int = 0,
    val duplicateDeletesUsed: Int = 0,
    val albumCount: Int = 0
)
