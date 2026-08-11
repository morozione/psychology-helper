package com.morozione.psychologyhelper.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.ui.theme.Dimens

@Composable
fun DailyCheckInCard(onMoodSelected: (Mood) -> Unit, modifier: Modifier = Modifier) {
    PsychologyCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Dimens.spaceXxl)) {
            Text("How are you feeling?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(Dimens.spaceLg))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Mood.entries.forEach { mood ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.radiusMd))
                            .clickable { onMoodSelected(mood) }
                            .padding(Dimens.spaceSm)
                    ) {
                        Text(mood.emoji, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(Dimens.spaceXs))
                        Text(mood.label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
