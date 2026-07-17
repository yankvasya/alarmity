package com.yankvasya.alarmity.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val ROUTE_ALARM_LIST = "alarm_list"

@Composable
fun AlarmityNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ROUTE_ALARM_LIST) {
        composable(ROUTE_ALARM_LIST) {
            AlarmListPlaceholder()
        }
    }
}

@Composable
private fun AlarmListPlaceholder() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Alarmity", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Alarm list coming in the next milestone", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
