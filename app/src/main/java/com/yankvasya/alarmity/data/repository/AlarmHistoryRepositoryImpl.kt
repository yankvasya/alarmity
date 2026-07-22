package com.yankvasya.alarmity.data.repository

import com.yankvasya.alarmity.data.local.AlarmHistoryDao
import com.yankvasya.alarmity.data.local.AlarmHistoryEntity
import com.yankvasya.alarmity.domain.model.AlarmStats
import com.yankvasya.alarmity.domain.model.computeCurrentStreak
import com.yankvasya.alarmity.domain.model.computeLongestStreak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class AlarmHistoryRepositoryImpl @Inject constructor(
    private val alarmHistoryDao: AlarmHistoryDao,
) : AlarmHistoryRepository {

    override suspend fun recordDismissed(alarmId: Long) = record(alarmId, dismissed = true)

    override suspend fun recordSnoozed(alarmId: Long) = record(alarmId, dismissed = false)

    private suspend fun record(alarmId: Long, dismissed: Boolean) {
        alarmHistoryDao.insert(
            AlarmHistoryEntity(alarmId = alarmId, timestampMillis = System.currentTimeMillis(), dismissed = dismissed),
        )
    }

    override fun observeStats(): Flow<AlarmStats> = alarmHistoryDao.observeAll().map { events ->
        val dismissalDates = events.filter { it.dismissed }.map { it.localDate() }.toSet()
        val today = LocalDate.now()
        AlarmStats(
            currentStreak = computeCurrentStreak(dismissalDates, today),
            longestStreak = computeLongestStreak(dismissalDates),
            totalDismissals = events.count { it.dismissed },
            totalSnoozes = events.count { !it.dismissed },
            last7Days = (6 downTo 0).map { offset -> today.minusDays(offset.toLong()) in dismissalDates },
        )
    }

    private fun AlarmHistoryEntity.localDate(): LocalDate =
        Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault()).toLocalDate()
}
