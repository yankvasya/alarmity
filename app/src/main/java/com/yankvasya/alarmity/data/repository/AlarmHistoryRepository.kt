package com.yankvasya.alarmity.data.repository

import com.yankvasya.alarmity.domain.model.AlarmStats
import kotlinx.coroutines.flow.Flow

interface AlarmHistoryRepository {
    suspend fun recordDismissed(alarmId: Long)
    suspend fun recordSnoozed(alarmId: Long)
    fun observeStats(): Flow<AlarmStats>
}
