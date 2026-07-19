package com.yankvasya.alarmity.ui.ringing

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yankvasya.alarmity.alarm.scheduler.AlarmScheduler
import com.yankvasya.alarmity.alarm.service.RingingService
import com.yankvasya.alarmity.data.repository.AlarmRepository
import com.yankvasya.alarmity.data.settings.SettingsRepository
import com.yankvasya.alarmity.domain.dismiss.DismissMission
import com.yankvasya.alarmity.domain.dismiss.DismissMissionRegistry
import com.yankvasya.alarmity.domain.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

private const val KEY_ALARM_ID = "alarmId"

@HiltViewModel
class RingingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val dismissMissionRegistry: DismissMissionRegistry,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val alarmId: Long = savedStateHandle.get<Long>(KEY_ALARM_ID) ?: -1L

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm.asStateFlow()

    private val _dismissMission = MutableStateFlow<DismissMission?>(null)
    val dismissMission: StateFlow<DismissMission?> = _dismissMission.asStateFlow()

    private val _finished = MutableStateFlow(false)
    val finished: StateFlow<Boolean> = _finished.asStateFlow()

    init {
        viewModelScope.launch {
            val loaded = alarmRepository.getAlarm(alarmId)
            _alarm.value = loaded
            _dismissMission.value = dismissMissionRegistry.get(
                loaded?.dismissMissionId ?: DismissMission.DEFAULT_MISSION_ID,
            )
        }
    }

    fun dismiss() {
        RingingService.stop(context)
        _finished.value = true
    }

    fun snooze() {
        RingingService.stop(context)
        viewModelScope.launch {
            val snoozeMinutes = settingsRepository.settings.first().snoozeMinutes
            val triggerAtMillis = LocalDateTime.now()
                .plusMinutes(snoozeMinutes)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            alarmScheduler.scheduleSnooze(alarmId, triggerAtMillis)
            _finished.value = true
        }
    }
}
