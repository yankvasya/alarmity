package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.runtime.Composable

interface DismissMission {
    val id: String

    /** String resource ID rather than a resolved String, since the display name must stay localized
     * even though missions are constructed once via DI, well outside any composition. */
    val displayNameRes: Int

    @Composable
    fun Content(onComplete: () -> Unit)

    companion object {
        const val DEFAULT_MISSION_ID = "simple_tap"
    }
}
