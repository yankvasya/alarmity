package com.yankvasya.alarmity.domain.model

data class Settings(
    val snoozeMinutes: Long = 10L,
    /** Null means the device's default alarm sound. */
    val alarmSoundUri: String? = null,
    val vibrationEnabled: Boolean = true,
)
