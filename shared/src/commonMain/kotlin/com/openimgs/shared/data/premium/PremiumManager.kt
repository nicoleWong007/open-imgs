package com.openimgs.shared.data.premium

import com.openimgs.shared.domain.model.PremiumFeature
import com.openimgs.shared.domain.model.PremiumStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Manages premium feature access and usage tracking.
 *
 * Free tier limits:
 * - 5 albums max
 * - 10 smart searches per month
 * - 5 duplicate deletes per day
 * - Storage analysis: view only (no batch cleanup)
 */
class PremiumManager(
    private var status: PremiumStatus,
    private val clock: Clock = Clock.System
) {

    fun currentStatus(): PremiumStatus = status

    fun isPremium(): Boolean {
        if (!status.isPremium) return false
        val expiry = status.expiryDate ?: return true
        return clock.now() < expiry
    }

    fun checkFeatureAccess(feature: PremiumFeature): Boolean {
        if (isPremium()) return true

        return when (feature) {
            PremiumFeature.UNLIMITED_ALBUMS -> status.albumCount < FREE_ALBUM_LIMIT
            PremiumFeature.UNLIMITED_SEARCH -> status.searchesUsed < FREE_SEARCH_MONTHLY_LIMIT
            PremiumFeature.UNLIMITED_DUPLICATE_DELETE -> status.duplicateDeletesUsed < FREE_DUPLICATE_DELETE_DAILY_LIMIT
            PremiumFeature.BATCH_CLEANUP -> false
        }
    }

    fun getRemainingUsage(feature: PremiumFeature): Int {
        if (isPremium()) return Int.MAX_VALUE

        return when (feature) {
            PremiumFeature.UNLIMITED_ALBUMS -> maxOf(0, FREE_ALBUM_LIMIT - status.albumCount)
            PremiumFeature.UNLIMITED_SEARCH -> maxOf(0, FREE_SEARCH_MONTHLY_LIMIT - status.searchesUsed)
            PremiumFeature.UNLIMITED_DUPLICATE_DELETE -> maxOf(0, FREE_DUPLICATE_DELETE_DAILY_LIMIT - status.duplicateDeletesUsed)
            PremiumFeature.BATCH_CLEANUP -> 0
        }
    }

    fun canCreateAlbum(): Boolean = checkFeatureAccess(PremiumFeature.UNLIMITED_ALBUMS)

    fun canSearch(): Boolean = checkFeatureAccess(PremiumFeature.UNLIMITED_SEARCH)

    fun canDeleteDuplicate(): Boolean = checkFeatureAccess(PremiumFeature.UNLIMITED_DUPLICATE_DELETE)

    fun canBatchCleanup(): Boolean = checkFeatureAccess(PremiumFeature.BATCH_CLEANUP)

    fun recordSearchUsage() {
        if (!isPremium()) {
            status = status.copy(searchesUsed = status.searchesUsed + 1)
        }
    }

    fun recordDuplicateDeleteUsage() {
        if (!isPremium()) {
            status = status.copy(duplicateDeletesUsed = status.duplicateDeletesUsed + 1)
        }
    }

    fun recordAlbumCreated() {
        status = status.copy(albumCount = status.albumCount + 1)
    }

    fun recordAlbumDeleted() {
        status = status.copy(albumCount = maxOf(0, status.albumCount - 1))
    }

    fun updatePremiumStatus(isPremium: Boolean, expiryDate: Instant? = null) {
        status = status.copy(
            isPremium = isPremium,
            expiryDate = expiryDate
        )
    }

    fun resetDailyUsage() {
        status = status.copy(duplicateDeletesUsed = 0)
    }

    fun resetMonthlyUsage() {
        status = status.copy(searchesUsed = 0)
    }

    companion object {
        const val FREE_ALBUM_LIMIT = 5
        const val FREE_SEARCH_MONTHLY_LIMIT = 10
        const val FREE_DUPLICATE_DELETE_DAILY_LIMIT = 5
    }
}
