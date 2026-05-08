package com.cvp.app.domain.usecase

/**
 * Tracks which zones have triggered a notification in the current session.
 * Each zone only fires once per session; [clearSession] resets the state
 * (e.g., when the app is foregrounded again after a long gap).
 */
class ShouldNotifyUseCase {

    private val notifiedIds = mutableSetOf<String>()

    fun shouldNotify(zoneId: String): Boolean {
        if (zoneId in notifiedIds) return false
        notifiedIds += zoneId
        return true
    }

    fun clearSession() {
        notifiedIds.clear()
    }
}
