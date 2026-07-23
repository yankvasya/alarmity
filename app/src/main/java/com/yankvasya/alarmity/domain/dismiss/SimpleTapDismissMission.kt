package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yankvasya.alarmity.R
import com.yankvasya.alarmity.ui.common.pressScale
import javax.inject.Inject

class SimpleTapDismissMission @Inject constructor() : DismissMission {
    override val id: String = DismissMission.DEFAULT_MISSION_ID
    override val displayNameRes: Int = R.string.mission_simple_tap

    @Composable
    override fun Content(onComplete: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            Button(
                onClick = onComplete,
                interactionSource = interactionSource,
                modifier = Modifier.pressScale(interactionSource),
            ) {
                Text(stringResource(R.string.mission_simple_tap))
            }
        }
    }
}
