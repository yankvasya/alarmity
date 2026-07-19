package com.yankvasya.alarmity.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Compose-native placeholders for the illustrative Lottie animations called for in the
 * original design (ring screen, empty state) — swap these call sites for LottieAnimation
 * composables once real .json assets are available; nothing else about the screens needs to change.
 */
@Composable
fun WigglingAlarmIcon(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ringingWiggle")
    val angle by transition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(160, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "angle",
    )
    Icon(
        imageVector = Icons.Filled.Alarm,
        contentDescription = null,
        modifier = modifier
            .size(96.dp)
            .graphicsLayer { rotationZ = angle },
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun FloatingAlarmOffIcon(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "emptyStateFloat")
    val offsetY by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "offsetY",
    )
    Icon(
        imageVector = Icons.Filled.AlarmOff,
        contentDescription = null,
        modifier = modifier
            .size(64.dp)
            .graphicsLayer { translationY = offsetY },
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
