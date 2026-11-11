package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth

import com.saifee.inventorysalesmanagement.mvvm.core.pref.SharedPref
import javax.inject.Inject
import javax.inject.Singleton

enum class SubscriptionStatus {
    ACTIVE,
    GRACE_PERIOD,
    EXPIRED
}

@Singleton
class CheckSubscriptionGracePeriodUseCase @Inject constructor(
    private val prefs: SharedPref
) {
    fun execute(): SubscriptionStatus {
        val currentTime = System.currentTimeMillis()
        val expiryTime = prefs.subscriptionExpiryTimestamp
        val gracePeriodDurationMs = 72L * 60 * 60 * 1000 // 72 hours

        if (currentTime <= expiryTime) {
            return SubscriptionStatus.ACTIVE
        }

        val timeSinceExpiry = currentTime - expiryTime
        if (timeSinceExpiry <= gracePeriodDurationMs) {
            return SubscriptionStatus.GRACE_PERIOD
        }

        return SubscriptionStatus.EXPIRED
    }
}
