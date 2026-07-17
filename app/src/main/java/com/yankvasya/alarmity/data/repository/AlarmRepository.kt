package com.yankvasya.alarmity.data.repository

import com.yankvasya.alarmity.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun observeAlarms(): Flow<List<Alarm>>
    suspend fun getAlarm(id: Long): Alarm?
    suspend fun saveAlarm(alarm: Alarm): Long
    suspend fun deleteAlarm(alarm: Alarm)
    suspend fun setEnabled(id: Long, enabled: Boolean)
}
