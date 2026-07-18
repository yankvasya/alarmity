package com.yankvasya.alarmity.ui.settings

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yankvasya.alarmity.domain.model.Settings

private val SNOOZE_OPTIONS_MINUTES = listOf(5L, 10L, 15L, 20L)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        SettingsContent(
            settings = settings,
            onSnoozeMinutesChange = viewModel::setSnoozeMinutes,
            onAlarmSoundUriChange = viewModel::setAlarmSoundUri,
            onVibrationEnabledChange = viewModel::setVibrationEnabled,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SettingsContent(
    settings: Settings,
    onSnoozeMinutesChange: (Long) -> Unit,
    onAlarmSoundUriChange: (String?) -> Unit,
    onVibrationEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val soundPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            onAlarmSoundUriChange(uri?.toString())
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Snooze duration",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SNOOZE_OPTIONS_MINUTES.forEach { minutes ->
                FilterChip(
                    selected = settings.snoozeMinutes == minutes,
                    onClick = { onSnoozeMinutesChange(minutes) },
                    label = { Text("$minutes min") },
                )
            }
        }

        val soundTitle = remember(settings.alarmSoundUri) {
            settings.alarmSoundUri
                ?.let { Uri.parse(it) }
                ?.let { RingtoneManager.getRingtone(context, it)?.getTitle(context) }
                ?: "Default"
        }
        ListItem(
            modifier = Modifier.clickable {
                val intent = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
                    .let { current -> buildRingtonePickerIntent(current) }
                soundPickerLauncher.launch(intent)
            },
            headlineContent = { Text("Alarm sound") },
            supportingContent = { Text(soundTitle) },
        )

        ListItem(
            modifier = Modifier.clickable { onVibrationEnabledChange(!settings.vibrationEnabled) },
            headlineContent = { Text("Vibration") },
            trailingContent = {
                Switch(checked = settings.vibrationEnabled, onCheckedChange = onVibrationEnabledChange)
            },
        )
    }
}

private fun buildRingtonePickerIntent(existingUri: Uri?) =
    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingUri)
    }
