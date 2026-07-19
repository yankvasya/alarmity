package com.yankvasya.alarmity.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yankvasya.alarmity.ui.alarmlist.AlarmListScreen
import com.yankvasya.alarmity.ui.editalarm.EditAlarmScreen
import com.yankvasya.alarmity.ui.editalarm.NEW_ALARM_ID
import com.yankvasya.alarmity.ui.ringing.RingingScreen
import com.yankvasya.alarmity.ui.settings.SettingsScreen

private const val ROUTE_ALARM_LIST = "alarm_list"
private const val ROUTE_SETTINGS = "settings"
private const val ARG_ALARM_ID = "alarmId"
private const val ROUTE_EDIT_ALARM = "edit_alarm?alarmId={$ARG_ALARM_ID}"
private const val ROUTE_RINGING = "ringing/{$ARG_ALARM_ID}"

private fun editAlarmRoute(alarmId: Long = NEW_ALARM_ID) = "edit_alarm?alarmId=$alarmId"
private fun ringingRoute(alarmId: Long) = "ringing/$alarmId"

@Composable
fun AlarmityNavHost(ringingAlarmId: Long? = null) {
    val navController = rememberNavController()

    LaunchedEffect(ringingAlarmId) {
        if (ringingAlarmId != null) {
            navController.navigate(ringingRoute(ringingAlarmId))
        }
    }

    NavHost(navController = navController, startDestination = ROUTE_ALARM_LIST) {
        composable(ROUTE_ALARM_LIST) {
            AlarmListScreen(
                onAddAlarm = { navController.navigate(editAlarmRoute()) },
                onEditAlarm = { alarmId -> navController.navigate(editAlarmRoute(alarmId)) },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = ROUTE_EDIT_ALARM,
            arguments = listOf(navArgument(ARG_ALARM_ID) { type = NavType.LongType; defaultValue = NEW_ALARM_ID }),
        ) {
            EditAlarmScreen(onDone = { navController.popBackStack() })
        }
        composable(
            route = ROUTE_RINGING,
            arguments = listOf(navArgument(ARG_ALARM_ID) { type = NavType.LongType }),
        ) {
            RingingScreen(onFinished = { navController.popBackStack() })
        }
    }
}
