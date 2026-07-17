package com.yankvasya.alarmity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.yankvasya.alarmity.navigation.AlarmityNavHost
import com.yankvasya.alarmity.ui.theme.AlarmityTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlarmityTheme {
                AlarmityNavHost()
            }
        }
    }
}
