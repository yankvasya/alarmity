package com.yankvasya.alarmity.data.repository

import com.yankvasya.alarmity.data.local.AlarmDao
import com.yankvasya.alarmity.data.local.AlarmEntity
import com.yankvasya.alarmity.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
) : AlarmRepository {

    override fun observeAlarms(): Flow<List<Alarm>> =
        alarmDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAlarm(id: Long): Alarm? =
        alarmDao.getById(id)?.toDomain()

    override suspend fun saveAlarm(alarm: Alarm): Long =
        alarmDao.upsert(alarm.toEntity())

    override suspend fun deleteAlarm(alarm: Alarm) =
        alarmDao.delete(alarm.toEntity())

    override suspend fun setEnabled(id: Long, enabled: Boolean) =
        alarmDao.setEnabled(id, enabled)

    private fun AlarmEntity.toDomain() = Alarm(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        enabled = enabled,
        repeatDays = repeatDays,
        dismissMissionId = dismissMissionId,
    )

    private fun Alarm.toEntity() = AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        label = label,
        enabled = enabled,
        repeatDays = repeatDays,
        dismissMissionId = dismissMissionId,
    )
}
