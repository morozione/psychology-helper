package com.morozione.psychologyhelper.feature.mood

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.ui.component.EmptyState
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyTextField
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.theme.MoodBad
import com.morozione.psychologyhelper.ui.theme.MoodGood
import com.morozione.psychologyhelper.ui.theme.MoodGreat
import com.morozione.psychologyhelper.ui.theme.MoodOkay
import com.morozione.psychologyhelper.ui.theme.MoodTerrible
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun moodColor(mood: Mood): Color = when (mood) {
    Mood.GREAT -> MoodGreat
    Mood.GOOD -> MoodGood
    Mood.OKAY -> MoodOkay
    Mood.BAD -> MoodBad
    Mood.TERRIBLE -> MoodTerrible
}

@Composable
fun Screen.MoodScreenContent(userId: String) {
    val screenModel = koinScreenModel<MoodScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            screenModel.onIntent(MoodIntent.Initialize(userId))
        }
    }

    LaunchedEffect(Unit) {
        screenModel.effects.collect { effect ->
            when (effect) {
                is MoodEffect.SavedSuccessfully -> { /* success is shown via state */ }
                is MoodEffect.ShowError -> { /* error shown inline */ }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.spaceLg)
    ) {
        item {
            Spacer(Modifier.height(Dimens.spaceXxl))
            Text(
                text = "Track Your Mood",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "How are you feeling right now?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(Dimens.spaceXl))
        }

        item {
            SectionTitle("Select Your Mood")
            Spacer(Modifier.height(Dimens.spaceSm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Mood.entries.forEach { mood ->
                    MoodCard(
                        mood = mood,
                        isSelected = state.selectedMood == mood,
                        onClick = { screenModel.onIntent(MoodIntent.SelectMood(mood)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(Dimens.spaceLg))
        }

        item {
            PsychologyTextField(
                value = state.note,
                onValueChange = { screenModel.onIntent(MoodIntent.UpdateNote(it)) },
                label = "Add a note (optional)",
                placeholder = "How's your day going?",
                singleLine = false,
                minLines = 3,
                maxLines = 5
            )
            Spacer(Modifier.height(Dimens.spaceLg))
        }

        if (state.error != null) {
            item {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(Dimens.spaceSm))
            }
        }

        item {
            PsychologyButton(
                text = if (state.saveSuccess) "Saved!" else "Save Mood",
                onClick = { screenModel.onIntent(MoodIntent.Save) },
                isLoading = state.isSaving,
                enabled = !state.saveSuccess
            )
            Spacer(Modifier.height(Dimens.spaceXxl))
            HorizontalDivider()
            Spacer(Modifier.height(Dimens.spaceSm))
            SectionTitle("Past Entries")
        }

        if (state.entries.isEmpty()) {
            item {
                EmptyState(
                    icon = "📊",
                    message = "No mood entries yet",
                    description = "Log your first mood entry above"
                )
            }
        } else {
            items(state.entries) { entry ->
                MoodHistoryItem(entry = entry)
                Spacer(Modifier.height(Dimens.spaceSm))
            }
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }
    }
}

@Composable
private fun MoodCard(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "mood_scale"
    )
    val bgColor = if (isSelected) moodColor(mood).copy(alpha = 0.25f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .padding(horizontal = Dimens.spaceXxs)
            .scale(scale)
            .clip(RoundedCornerShape(Dimens.radiusMd))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = Dimens.spaceSm, horizontal = Dimens.spaceXs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = mood.emoji, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(Dimens.spaceXxs))
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = if (isSelected) moodColor(mood) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MoodHistoryItem(entry: MoodEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = entry.mood.emoji, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.width(Dimens.spaceMd))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.mood.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (entry.note.isNotBlank()) {
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2
                )
            }
        }
        Spacer(Modifier.width(Dimens.spaceSm))
        Text(
            text = formatDateTime(entry.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

private fun formatDateTime(timestamp: Long): String {
    val dt = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.dayOfMonth}/${dt.monthNumber}\n${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
}
