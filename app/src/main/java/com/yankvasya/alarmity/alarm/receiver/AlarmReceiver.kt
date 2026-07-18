package com.yankvasya.alarmity.alarm.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.yankvasya.alarmity.R
import com.yankvasya.alarmity.alarm.scheduler.AlarmScheduler
import com.yankvasya.alarmity.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fires when a scheduled alarm's trigger time is reached. Posts a plain notification for now —
 * this is a placeholder for the full-screen ringing experience (foreground service, ring screen,
 * dismiss mission) that replaces it in the next milestone. Proving this path fires reliably is
 * the point of shipping it before building anything on top of it.
 */
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
                showFiringNotification(context, alarm.label)
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

    private fun showFiringNotification(context: Context, label: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm")
            .setContentText(label.ifBlank { "Time to wake up" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val EXTRA_ALARM_ID = "alarm_id"
        const val CHANNEL_ID = "alarm_firing"
        private const val NOTIFICATION_ID = 1001

        fun newIntent(context: Context, alarmId: Long): Intent =
            Intent(context, AlarmReceiver::class.java).putExtra(EXTRA_ALARM_ID, alarmId)
    }
}
