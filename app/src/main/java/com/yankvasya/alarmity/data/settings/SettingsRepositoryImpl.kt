package com.yankvasya.alarmity.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.yankvasya.alarmity.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object Keys {
    val SNOOZE_MINUTES = longPreferencesKey("snooze_minutes")
    val ALARM_SOUND_URI = stringPreferencesKey("alarm_sound_uri")
    val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
}

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<Settings> = dataStore.data.map { prefs ->
        val defaults = Settings()
        Settings(
            snoozeMinutes = prefs[Keys.SNOOZE_MINUTES] ?: defaults.snoozeMinutes,
            alarmSoundUri = prefs[Keys.ALARM_SOUND_URI],
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: defaults.vibrationEnabled,
        )
    }

    override suspend fun setSnoozeMinutes(minutes: Long) {
        dataStore.edit { it[Keys.SNOOZE_MINUTES] = minutes }
    }

    override suspend fun setAlarmSoundUri(uri: String?) {
        dataStore.edit { prefs ->
            if (uri == null) prefs.remove(Keys.ALARM_SOUND_URI) else prefs[Keys.ALARM_SOUND_URI] = uri
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }
}
