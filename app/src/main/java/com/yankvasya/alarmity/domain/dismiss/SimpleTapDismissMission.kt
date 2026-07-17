package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import javax.inject.Inject

class SimpleTapDismissMission @Inject constructor() : DismissMission {
    override val id: String = DismissMission.DEFAULT_MISSION_ID
    override val displayName: String = "Tap to dismiss"

    @Composable
    override fun Content(onComplete: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Button(onClick = onComplete) {
                Text(displayName)
            }
        }
    }
}
