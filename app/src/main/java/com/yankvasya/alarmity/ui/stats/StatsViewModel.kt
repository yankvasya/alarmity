package com.yankvasya.alarmity.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yankvasya.alarmity.data.repository.AlarmHistoryRepository
import com.yankvasya.alarmity.domain.model.AlarmStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    alarmHistoryRepository: AlarmHistoryRepository,
) : ViewModel() {

    val stats: StateFlow<AlarmStats> = alarmHistoryRepository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmStats.EMPTY)
}
