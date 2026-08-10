package com.morozione.psychologyhelper.feature.mood

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.koinScreenModel
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.ui.component.EmptyState
import com.morozione.psychologyhelper.ui.component.MoodChip
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyTextField
import com.morozione.psychologyhelper.ui.component.SectionTitle
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MoodScreenContent(userId: String) {
    val screenModel = koinScreenModel<MoodScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            screenModel.initialize(userId)
        }
    }

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            screenModel.clearSaveSuccess()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
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
            Spacer(Modifier.height(20.dp))
        }

        item {
            SectionTitle("Select Your Mood")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Mood.entries.forEach { mood ->
                    MoodChip(
                        emoji = mood.emoji,
                        label = mood.label,
                        isSelected = state.selectedMood == mood,
                        onClick = { screenModel.selectMood(mood) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        item {
            PsychologyTextField(
                value = state.note,
                onValueChange = screenModel::onNoteChanged,
                label = "Add a note (optional)",
                placeholder = "How's your day going?",
                singleLine = false,
                minLines = 3,
                maxLines = 5
            )
            Spacer(Modifier.height(16.dp))
        }

        if (state.error != null) {
            item {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            PsychologyButton(
                text = if (state.saveSuccess) "Saved!" else "Save Mood",
                onClick = screenModel::saveMoodEntry,
                isLoading = state.isSaving,
                enabled = !state.saveSuccess
            )
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
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
                Spacer(Modifier.height(8.dp))
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun MoodHistoryItem(entry: MoodEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = entry.mood.emoji, fontSize = 28.sp)
        Spacer(Modifier.width(12.dp))
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
        Spacer(Modifier.width(8.dp))
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
