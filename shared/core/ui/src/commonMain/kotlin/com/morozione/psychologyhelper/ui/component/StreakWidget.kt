package com.morozione.psychologyhelper.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.morozione.psychologyhelper.ui.theme.Dimens

@Composable
fun StreakWidget(streakDays: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusFull))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔥", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(Dimens.spaceXs))
        Text(
            "$streakDays day streak",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
