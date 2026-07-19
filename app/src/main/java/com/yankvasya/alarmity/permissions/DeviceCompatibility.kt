package com.yankvasya.alarmity.permissions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

/**
 * Some OEMs run aggressive "autostart"/protected-apps kill lists on top of stock Android's own
 * Doze/App Standby, and standard Doze exemption doesn't cover them. These component names are
 * unofficial and undocumented by the OEMs (sourced from the community-maintained AutoStarter
 * project) and can break across ROM updates, so every launch attempt falls back gracefully.
 */
object DeviceCompatibility {

    private val AUTOSTART_COMPONENTS: Map<String, ComponentName> = mapOf(
        "xiaomi" to ComponentName(
            "com.miui.securitycenter",
            "com.miui.permcenter.autostart.AutoStartManagementActivity",
        ),
        "huawei" to ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
        ),
        "honor" to ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.systemmanager.optimize.process.ProtectActivity",
        ),
        "oneplus" to ComponentName(
            "com.oneplus.security",
            "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity",
        ),
        "samsung" to ComponentName(
            "com.samsung.android.lool",
            "com.samsung.android.sm.ui.battery.BatteryActivity",
        ),
    )

    fun isKnownProblematicManufacturer(): Boolean =
        Build.MANUFACTURER.lowercase() in AUTOSTART_COMPONENTS.keys

    fun isIgnoringBatteryOptimizations(context: Context): Boolean =
        context.getSystemService(PowerManager::class.java)
            .isIgnoringBatteryOptimizations(context.packageName)

    /** The OEM's own autostart/protected-apps screen, if resolvable on this device; null otherwise. */
    fun autoStartSettingsIntent(context: Context): Intent? {
        val component = AUTOSTART_COMPONENTS[Build.MANUFACTURER.lowercase()] ?: return null
        val intent = Intent().setComponent(component)
        return intent.takeIf { it.resolveActivity(context.packageManager) != null }
    }

    /**
     * Opens the OS's general battery-optimization list rather than requesting direct exemption:
     * Google Play's acceptable-use-case table for REQUEST_IGNORE_BATTERY_OPTIMIZATIONS doesn't
     * list alarm-clock apps, and the core alarm-firing path already bypasses Doze via
     * AlarmManager.setAlarmClock() without needing that permission.
     */
    fun batteryOptimizationSettingsIntent(): Intent =
        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)

    fun appSettingsIntent(context: Context): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
}
