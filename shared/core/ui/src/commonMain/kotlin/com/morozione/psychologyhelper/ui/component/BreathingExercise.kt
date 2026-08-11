package com.morozione.psychologyhelper.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BreathingPhase(val instruction: String, val durationSeconds: Int) {
    INHALE("Inhale", 4),
    HOLD("Hold", 7),
    EXHALE("Exhale", 8)
}

@Composable
fun BreathingCircle(
    phase: BreathingPhase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (phase == BreathingPhase.INHALE || phase == BreathingPhase.HOLD) 1f else 0.6f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "breathing"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = phase.instruction,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}
