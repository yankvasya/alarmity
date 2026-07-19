package com.yankvasya.alarmity.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yankvasya.alarmity.data.settings.SettingsRepository
import com.yankvasya.alarmity.domain.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Settings())

    fun setSnoozeMinutes(minutes: Long) {
        viewModelScope.launch { settingsRepository.setSnoozeMinutes(minutes) }
    }

    fun setAlarmSoundUri(uri: String?) {
        viewModelScope.launch { settingsRepository.setAlarmSoundUri(uri) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setVibrationEnabled(enabled) }
    }
}
