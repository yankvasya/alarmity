package com.yankvasya.alarmity.ui.editalarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yankvasya.alarmity.data.repository.AlarmRepository
import com.yankvasya.alarmity.domain.dismiss.DismissMission
import com.yankvasya.alarmity.domain.dismiss.DismissMissionRegistry
import com.yankvasya.alarmity.domain.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

private const val KEY_ALARM_ID = "alarmId"
const val NEW_ALARM_ID = -1L

data class EditAlarmUiState(
    val alarmId: Long = NEW_ALARM_ID,
    val hour: Int = LocalTime.now().hour,
    val minute: Int = LocalTime.now().minute,
    val label: String = "",
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val enabled: Boolean = true,
    val dismissMissionId: String = DismissMission.DEFAULT_MISSION_ID,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
) {
    val isNew: Boolean get() = alarmId == NEW_ALARM_ID
}

@HiltViewModel
class EditAlarmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val alarmRepository: AlarmRepository,
    dismissMissionRegistry: DismissMissionRegistry,
) : ViewModel() {

    val availableMissions: List<DismissMission> = dismissMissionRegistry.all

    private val _uiState = MutableStateFlow(
        EditAlarmUiState(alarmId = savedStateHandle.get<Long>(KEY_ALARM_ID) ?: NEW_ALARM_ID),
    )
    val uiState: StateFlow<EditAlarmUiState> = _uiState.asStateFlow()

    init {
        val alarmId = _uiState.value.alarmId
        if (alarmId != NEW_ALARM_ID) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                alarmRepository.getAlarm(alarmId)?.let { alarm ->
                    _uiState.update {
                        it.copy(
                            hour = alarm.hour,
                            minute = alarm.minute,
                            label = alarm.label,
                            repeatDays = alarm.repeatDays,
                            enabled = alarm.enabled,
                            dismissMissionId = alarm.dismissMissionId,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    fun setTime(hour: Int, minute: Int) = _uiState.update { it.copy(hour = hour, minute = minute) }

    fun setLabel(label: String) = _uiState.update { it.copy(label = label) }

    fun toggleRepeatDay(day: DayOfWeek) = _uiState.update {
        it.copy(repeatDays = if (day in it.repeatDays) it.repeatDays - day else it.repeatDays + day)
    }

    fun setDismissMission(id: String) = _uiState.update { it.copy(dismissMissionId = id) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            alarmRepository.saveAlarm(
                Alarm(
                    id = state.alarmId.takeIf { it != NEW_ALARM_ID } ?: 0,
                    hour = state.hour,
                    minute = state.minute,
                    label = state.label,
                    repeatDays = state.repeatDays,
                    enabled = state.enabled,
                    dismissMissionId = state.dismissMissionId,
                ),
            )
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun delete() {
        val state = _uiState.value
        if (state.isNew) return
        viewModelScope.launch {
            alarmRepository.getAlarm(state.alarmId)?.let { alarmRepository.deleteAlarm(it) }
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
