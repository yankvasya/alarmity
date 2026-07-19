package com.yankvasya.alarmity.data.settings

import com.yankvasya.alarmity.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<Settings>
    suspend fun setSnoozeMinutes(minutes: Long)
    suspend fun setAlarmSoundUri(uri: String?)
    suspend fun setVibrationEnabled(enabled: Boolean)
}
