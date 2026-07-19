package com.yankvasya.alarmity.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yankvasya.alarmity.alarm.scheduler.AlarmScheduler
import com.yankvasya.alarmity.alarm.service.RingingService
import com.yankvasya.alarmity.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Fires when a scheduled alarm's trigger time is reached and starts the ringing experience. */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmRepository: AlarmRepository

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarm = alarmRepository.getAlarm(alarmId) ?: return@launch
                RingingService.start(context, alarmId)
                if (alarm.isOneOff) {
                    alarmRepository.setEnabled(alarm.id, false)
                } else {
                    alarmScheduler.schedule(alarm)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val EXTRA_ALARM_ID = "alarm_id"
        const val CHANNEL_ID = "alarm_firing"

        fun newIntent(context: Context, alarmId: Long): Intent =
            Intent(context, AlarmReceiver::class.java).putExtra(EXTRA_ALARM_ID, alarmId)
    }
}
