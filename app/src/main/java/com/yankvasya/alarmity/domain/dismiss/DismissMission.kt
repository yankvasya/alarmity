package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.runtime.Composable

interface DismissMission {
    val id: String
    val displayName: String

    @Composable
    fun Content(onComplete: () -> Unit)

    companion object {
        const val DEFAULT_MISSION_ID = "simple_tap"
    }
}
