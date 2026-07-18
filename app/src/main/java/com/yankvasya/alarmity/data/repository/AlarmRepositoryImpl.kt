package com.yankvasya.alarmity.data.repository

import com.yankvasya.alarmity.alarm.scheduler.AlarmScheduler
import com.yankvasya.alarmity.data.local.AlarmDao
import com.yankvasya.alarmity.data.local.AlarmEntity
import com.yankvasya.alarmity.domain.model.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Keeps AlarmManager's scheduled alarms in sync with every write, so callers never have to. */
class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmScheduler: AlarmScheduler,
) : AlarmRepository {

    override fun observeAlarms(): Flow<List<Alarm>> =
        alarmDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAlarm(id: Long): Alarm? =
        alarmDao.getById(id)?.toDomain()

    override suspend fun saveAlarm(alarm: Alarm): Long {
        val id = alarmDao.upsert(alarm.toEntity())
        applySchedule(alarm.copy(id = id))
        return id
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.delete(alarm.toEntity())
        alarmScheduler.cancel(alarm.id)
    }

    override suspend fun setEnabled(id: Long, enabled: Boolean) {
        alarmDao.setEnabled(id, enabled)
        getAlarm(id)?.let { applySchedule(it) }
    }

    private fun applySchedule(alarm: Alarm) {
        if (alarm.enabled) alarmScheduler.schedule(alarm) else alarmScheduler.cancel(alarm.id)
    }

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
