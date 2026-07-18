package com.yankvasya.alarmity

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.yankvasya.alarmity.alarm.receiver.AlarmReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlarmityApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            AlarmReceiver.CHANNEL_ID,
            "Alarms",
            NotificationManager.IMPORTANCE_HIGH,
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
