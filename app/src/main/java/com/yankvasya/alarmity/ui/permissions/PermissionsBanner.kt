package com.yankvasya.alarmity.ui.permissions

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yankvasya.alarmity.permissions.DeviceCompatibility

private data class PermissionsBannerState(
    val needsNotificationPermission: Boolean,
    val needsBatteryOptimizationFix: Boolean,
)

private fun computeState(context: Context): PermissionsBannerState = PermissionsBannerState(
    needsNotificationPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED,
    needsBatteryOptimizationFix = DeviceCompatibility.isKnownProblematicManufacturer() &&
        !DeviceCompatibility.isIgnoringBatteryOptimizations(context),
)

/**
 * Surfaces outstanding reliability issues (missing notification permission, OEM battery/autostart
 * restrictions) above the alarm list rather than nagging once and being forgotten — alarms failing
 * silently in the background is the top complaint for this class of app.
 */
@Composable
fun PermissionsBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(computeState(context)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) state = computeState(context)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { state = computeState(context) }

    if (!state.needsNotificationPermission && !state.needsBatteryOptimizationFix) return

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.needsNotificationPermission) {
            BannerCard(
                message = "Allow notifications so alarms can show over the lock screen when they fire.",
                actionLabel = "Allow",
                onAction = { requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS) },
            )
        }
        if (state.needsBatteryOptimizationFix) {
            BannerCard(
                message = "${Build.MANUFACTURER} devices can silently kill alarms in the background. " +
                    "Exempt Alarmity from battery restrictions to keep alarms reliable.",
                actionLabel = "Fix",
                onAction = { launchBatteryFixIntent(context) },
            )
        }
    }
}

@Composable
private fun BannerCard(message: String, actionLabel: String, onAction: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

private fun launchBatteryFixIntent(context: Context) {
    val intent = DeviceCompatibility.autoStartSettingsIntent(context)
        ?: DeviceCompatibility.batteryOptimizationSettingsIntent()
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(DeviceCompatibility.appSettingsIntent(context))
    }
}
