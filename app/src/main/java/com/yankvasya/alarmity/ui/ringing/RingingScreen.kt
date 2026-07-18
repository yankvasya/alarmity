package com.yankvasya.alarmity.ui.ringing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun RingingScreen(
    onFinished: () -> Unit,
    viewModel: RingingViewModel = hiltViewModel(),
) {
    val alarm by viewModel.alarm.collectAsState()
    val dismissMission by viewModel.dismissMission.collectAsState()
    val finished by viewModel.finished.collectAsState()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                alarm?.let {
                    Text(
                        text = "%02d:%02d".format(it.hour, it.minute),
                        style = MaterialTheme.typography.displayLarge,
                    )
                    if (it.label.isNotBlank()) {
                        Text(text = it.label, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            dismissMission?.Content(onComplete = viewModel::dismiss)

            TextButton(onClick = viewModel::snooze) {
                Text("Snooze")
            }
        }
    }
}
