package com.yankvasya.alarmity

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import com.yankvasya.alarmity.navigation.AlarmityNavHost
import com.yankvasya.alarmity.ui.theme.AlarmityTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var ringingAlarmId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)
        setContent {
            AlarmityTheme {
                AlarmityNavHost(ringingAlarmId = ringingAlarmId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action != ACTION_SHOW_RINGING) return
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        showOverLockScreen()
        ringingAlarmId = alarmId
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            )
        }
        getSystemService(KeyguardManager::class.java)?.requestDismissKeyguard(this, null)
    }

    companion object {
        const val ACTION_SHOW_RINGING = "com.yankvasya.alarmity.action.SHOW_RINGING"
        const val EXTRA_ALARM_ID = "alarm_id"
    }
}
