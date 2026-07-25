package com.yankvasya.alarmity.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.yankvasya.alarmity.ui.stats.StatsScreen

private const val TRANSITION_DURATION_MILLIS = 280
private const val SLIDE_FRACTION = 4

private const val ROUTE_ALARM_LIST = "alarm_list"
private const val ROUTE_SETTINGS = "settings"
private const val ROUTE_STATS = "stats"
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

    NavHost(
        navController = navController,
        startDestination = ROUTE_ALARM_LIST,
        enterTransition = {
            slideInHorizontally(tween(TRANSITION_DURATION_MILLIS)) { it / SLIDE_FRACTION } +
                fadeIn(tween(TRANSITION_DURATION_MILLIS))
        },
        exitTransition = {
            slideOutHorizontally(tween(TRANSITION_DURATION_MILLIS)) { -it / SLIDE_FRACTION } +
                fadeOut(tween(TRANSITION_DURATION_MILLIS))
        },
        popEnterTransition = {
            slideInHorizontally(tween(TRANSITION_DURATION_MILLIS)) { -it / SLIDE_FRACTION } +
                fadeIn(tween(TRANSITION_DURATION_MILLIS))
        },
        popExitTransition = {
            slideOutHorizontally(tween(TRANSITION_DURATION_MILLIS)) { it / SLIDE_FRACTION } +
                fadeOut(tween(TRANSITION_DURATION_MILLIS))
        },
    ) {
        composable(ROUTE_ALARM_LIST) {
            AlarmListScreen(
                onAddAlarm = { navController.navigate(editAlarmRoute()) },
                onEditAlarm = { alarmId -> navController.navigate(editAlarmRoute(alarmId)) },
                onOpenSettings = { navController.navigate(ROUTE_SETTINGS) },
                onOpenStats = { navController.navigate(ROUTE_STATS) },
            )
        }
        composable(ROUTE_SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(ROUTE_STATS) {
            StatsScreen(onBack = { navController.popBackStack() })
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
