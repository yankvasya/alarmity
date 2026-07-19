package com.yankvasya.alarmity.alarm.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yankvasya.alarmity.MainActivity
import com.yankvasya.alarmity.alarm.receiver.AlarmReceiver
import com.yankvasya.alarmity.domain.model.Alarm
import com.yankvasya.alarmity.domain.model.nextTriggerTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
) {
    fun schedule(alarm: Alarm) {
        val triggerAtMillis = alarm.nextTriggerTime()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        scheduleAt(alarm.id, triggerAtMillis)
    }

    /** Schedules a one-shot wake at [triggerAtMillis], bypassing the alarm's own repeat schedule. */
    fun scheduleSnooze(alarmId: Long, triggerAtMillis: Long) {
        scheduleAt(alarmId, triggerAtMillis)
    }

    fun cancel(alarmId: Long) {
        alarmManager.cancel(alarmOperationPendingIntent(alarmId))
    }

    private fun scheduleAt(alarmId: Long, triggerAtMillis: Long) {
        val showIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val operation = alarmOperationPendingIntent(alarmId)

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent), operation)
    }

    private fun alarmOperationPendingIntent(alarmId: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            AlarmReceiver.newIntent(context, alarmId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
