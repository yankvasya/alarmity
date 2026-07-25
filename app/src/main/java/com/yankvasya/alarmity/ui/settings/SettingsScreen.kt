package com.yankvasya.alarmity.ui.settings

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yankvasya.alarmity.R
import com.yankvasya.alarmity.domain.model.Settings
import com.yankvasya.alarmity.ui.common.SectionCard

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
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
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

    val defaultSoundLabel = stringResource(R.string.sound_default)
    val soundTitle = remember(settings.alarmSoundUri, defaultSoundLabel) {
        settings.alarmSoundUri
            ?.let { Uri.parse(it) }
            ?.let { RingtoneManager.getRingtone(context, it)?.getTitle(context) }
            ?: defaultSoundLabel
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SectionCard(title = stringResource(R.string.snooze_duration)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SNOOZE_OPTIONS_MINUTES.forEach { minutes ->
                    FilterChip(
                        selected = settings.snoozeMinutes == minutes,
                        onClick = { onSnoozeMinutesChange(minutes) },
                        label = { Text(stringResource(R.string.minutes_format, minutes)) },
                    )
                }
            }
        }

        SectionCard {
            ListItem(
                modifier = Modifier.clickable {
                    val intent = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM)
                        .let { current -> buildRingtonePickerIntent(current) }
                    soundPickerLauncher.launch(intent)
                },
                headlineContent = { Text(stringResource(R.string.alarm_sound)) },
                supportingContent = { Text(soundTitle) },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            ListItem(
                modifier = Modifier.clickable { onVibrationEnabledChange(!settings.vibrationEnabled) },
                headlineContent = { Text(stringResource(R.string.vibration)) },
                trailingContent = {
                    Switch(checked = settings.vibrationEnabled, onCheckedChange = onVibrationEnabledChange)
                },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            )
        }

        SectionCard(title = stringResource(R.string.language_setting_label)) {
            LanguageSelector()
        }
    }
}

/**
 * Reads/writes the per-app language via AppCompatDelegate rather than our own Settings/DataStore
 * model: AndroidX already persists and reapplies the choice automatically (autoStoreLocales, see
 * the manifest), and setApplicationLocales() recreates the activity, which is what actually
 * refreshes every stringResource() call in the tree — a second storage path would just be
 * redundant and could drift out of sync with the real one AppCompat is using.
 */
@Composable
private fun LanguageSelector() {
    var selectedTag by remember {
        mutableStateOf(AppCompatDelegate.getApplicationLocales().get(0)?.language)
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LanguageChip(
            label = stringResource(R.string.language_system),
            selected = selectedTag == null,
            onClick = {
                selectedTag = null
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
            },
        )
        LanguageChip(
            label = stringResource(R.string.language_english),
            selected = selectedTag == "en",
            onClick = {
                selectedTag = "en"
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
            },
        )
        LanguageChip(
            label = stringResource(R.string.language_russian),
            selected = selectedTag == "ru",
            onClick = {
                selectedTag = "ru"
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru"))
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

private fun buildRingtonePickerIntent(existingUri: Uri?) =
    Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingUri)
    }
