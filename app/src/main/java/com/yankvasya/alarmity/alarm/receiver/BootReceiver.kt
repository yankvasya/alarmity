package com.yankvasya.alarmity.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yankvasya.alarmity.alarm.scheduler.AlarmScheduler
import com.yankvasya.alarmity.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** AlarmManager alarms don't survive a reboot, so every enabled alarm needs re-scheduling. */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmRepository: AlarmRepository

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                alarmRepository.observeAlarms().first()
                    .filter { it.enabled }
                    .forEach { alarmScheduler.schedule(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
