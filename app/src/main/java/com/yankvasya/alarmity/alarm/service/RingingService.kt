package com.yankvasya.alarmity.alarm.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.yankvasya.alarmity.MainActivity
import com.yankvasya.alarmity.R
import com.yankvasya.alarmity.alarm.receiver.AlarmReceiver
import com.yankvasya.alarmity.data.repository.AlarmRepository
import com.yankvasya.alarmity.data.settings.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Plays the alarm sound and vibration, and posts the full-screen-intent notification. */
@AndroidEntryPoint
class RingingService : Service() {

    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var settingsRepository: SettingsRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopRinging()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        if (alarmId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }
        startRinging(alarmId)
        return START_NOT_STICKY
    }

    private fun startRinging(alarmId: Long) {
        serviceScope.launch {
            val label = alarmRepository.getAlarm(alarmId)?.label.orEmpty()
            val settings = settingsRepository.settings.first()
            startForeground(
                NOTIFICATION_ID,
                buildNotification(alarmId, label),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK,
            )
            playSound(settings.alarmSoundUri)
            if (settings.vibrationEnabled) startVibration()
        }
    }

    private fun buildNotification(alarmId: Long, label: String): Notification {
        val fullScreenIntent = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            Intent(this, MainActivity::class.java).apply {
                action = MainActivity.ACTION_SHOW_RINGING
                putExtra(MainActivity.EXTRA_ALARM_ID, alarmId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, AlarmReceiver.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_alarm_title))
            .setContentText(label.ifBlank { getString(R.string.notification_time_to_wake_up) })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenIntent, true)
            .setContentIntent(fullScreenIntent)
            .setOngoing(true)
            .build()
    }

    private fun playSound(alarmSoundUri: String?) {
        val uri = alarmSoundUri?.let(Uri::parse)
            ?: RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
            )
            setDataSource(this@RingingService, uri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun startVibration() {
        val pattern = longArrayOf(0, 800, 800)
        vibrator().vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun vibrator(): Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }

    private fun stopRinging() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        vibrator().cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopRinging()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val ACTION_STOP = "com.yankvasya.alarmity.action.STOP_RINGING"
        private const val EXTRA_ALARM_ID = "alarm_id"
        private const val NOTIFICATION_ID = 2001

        fun start(context: Context, alarmId: Long) {
            val intent = Intent(context, RingingService::class.java).putExtra(EXTRA_ALARM_ID, alarmId)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.startService(Intent(context, RingingService::class.java).setAction(ACTION_STOP))
        }
    }
}
