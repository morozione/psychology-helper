package com.morozione.psychologyhelper.feature.journal

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.ui.component.EmptyState
import com.morozione.psychologyhelper.ui.component.ErrorContent
import com.morozione.psychologyhelper.ui.component.LoadingContent
import com.morozione.psychologyhelper.ui.component.PsychologyCard
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.theme.PrimaryGreen
import com.morozione.psychologyhelper.ui.theme.SecondaryTeal
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.JournalScreenContent(userId: String, navigator: Navigator) {
    val screenModel = koinScreenModel<JournalScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            screenModel.onIntent(JournalIntent.Initialize(userId))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.push(JournalEntryScreen(userId = userId)) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "New journal entry",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> LoadingContent()
                state.error != null -> ErrorContent(
                    message = state.error!!,
                    onRetry = { screenModel.onIntent(JournalIntent.Retry) }
                )
                state.entries.isEmpty() -> {
                    EmptyState(
                        icon = "📔",
                        message = "No journal entries yet",
                        description = "Tap the + button to write your first entry"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimens.spaceLg)
                    ) {
                        item {
                            Spacer(Modifier.height(Dimens.spaceLg))
                            Text(
                                text = "My Journal",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(Dimens.spaceLg))
                        }
                        items(state.entries) { entry ->
                            JournalEntryItem(
                                entry = entry,
                                onTap = {
                                    navigator.push(
                                        JournalEntryScreen(userId = userId, existingEntry = entry)
                                    )
                                },
                                onDelete = { screenModel.onIntent(JournalIntent.DeleteEntry(entry.id)) }
                            )
                            Spacer(Modifier.height(Dimens.spaceMd))
                        }
                        item { Spacer(Modifier.height(Dimens.space5xl)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun JournalEntryItem(
    entry: JournalEntry,
    onTap: () -> Unit,
    onDelete: () -> Unit
) {
    // Colored left border based on content length
    val borderColor = when {
        entry.content.length < 100 -> SecondaryTeal
        entry.content.length < 300 -> PrimaryGreen
        else -> MaterialTheme.colorScheme.primary
    }

    PsychologyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onTap
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spaceLg),
            verticalAlignment = Alignment.Top
        ) {
            // Left color indicator
            Box(
                modifier = Modifier
                    .width(Dimens.spaceXxs)
                    .height(Dimens.space4xl)
                    .then(
                        Modifier.padding(end = Dimens.spaceXxs)
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Dimens.spaceXs))
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(Dimens.spaceSm))
                Text(
                    text = formatDate(entry.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Spacer(Modifier.width(Dimens.spaceSm))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete entry",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val dt = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.dayOfMonth} ${dt.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dt.year}"
}
