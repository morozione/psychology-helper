package com.morozione.psychologyhelper.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.feature.chat.ChatScreenContent
import com.morozione.psychologyhelper.feature.journal.JournalScreenContent
import com.morozione.psychologyhelper.feature.mood.MoodScreenContent
import com.morozione.psychologyhelper.feature.profile.ProfileScreenContent
import com.morozione.psychologyhelper.ui.component.EmptyState
import com.morozione.psychologyhelper.ui.component.LoadingContent
import com.morozione.psychologyhelper.ui.component.PsychologyCard
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.theme.GradientEnd
import com.morozione.psychologyhelper.ui.theme.GradientStart
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class BottomTab(val label: String, val emoji: String) {
    HOME("Home", "🏠"),
    MOOD("Mood", "😊"),
    JOURNAL("Journal", "📔"),
    CHAT("Chat", "🤖"),
    PROFILE("Profile", "👤")
}

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<HomeScreenModel>()
        val state by screenModel.state.collectAsState()
        var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

        LaunchedEffect(Unit) {
            screenModel.effects.collect { effect ->
                when (effect) {
                    is HomeEffect.ShowError -> { /* handled inline */ }
                }
            }
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    BottomTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Text(text = tab.emoji, fontSize = 20.sp) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    BottomTab.HOME -> HomeDashboardContent(state = state, screenModel = screenModel)
                    BottomTab.MOOD -> MoodScreenContent(userId = state.user?.id ?: "")
                    BottomTab.JOURNAL -> JournalScreenContent(
                        userId = state.user?.id ?: "",
                        navigator = navigator
                    )
                    BottomTab.CHAT -> ChatScreenContent()
                    BottomTab.PROFILE -> ProfileScreenContent(user = state.user)
                }
            }
        }
    }
}

@Composable
private fun HomeDashboardContent(state: HomeState, screenModel: HomeScreenModel) {
    if (state.isLoading) {
        LoadingContent()
        return
    }

    val greeting = buildGreeting(state.user?.displayName ?: "")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.spaceLg)
    ) {
        item {
            Spacer(Modifier.height(Dimens.spaceXxl))

            // Gradient header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
                    .padding(Dimens.spaceXxl)
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        text = "How are you feeling?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(Modifier.height(Dimens.spaceXl))
        }

        item {
            SectionTitle("Quick Actions")
            Row(modifier = Modifier.fillMaxWidth()) {
                PsychologyCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.space5xl + Dimens.spaceXxl)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.spaceMd),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("😊", fontSize = 28.sp)
                        Spacer(Modifier.height(Dimens.spaceXs))
                        Text(
                            "Log Mood",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.width(Dimens.spaceMd))
                PsychologyCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.space5xl + Dimens.spaceXxl)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimens.spaceMd),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📔", fontSize = 28.sp)
                        Spacer(Modifier.height(Dimens.spaceXs))
                        Text(
                            "Write Journal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimens.spaceXl))
        }

        item { SectionTitle("Recent Mood Entries") }

        if (state.recentMoodEntries.isEmpty()) {
            item {
                EmptyState(
                    icon = "😐",
                    message = "No mood entries yet",
                    description = "Start tracking your mood to see patterns"
                )
            }
        } else {
            items(state.recentMoodEntries) { entry ->
                MoodEntryItem(entry = entry)
                Spacer(Modifier.height(Dimens.spaceSm))
            }
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }
    }
}

@Composable
private fun MoodEntryItem(entry: MoodEntry) {
    PsychologyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Dimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = entry.mood.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(Dimens.spaceMd))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.mood.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (entry.note.isNotBlank()) {
                    Text(
                        text = entry.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
            Spacer(Modifier.width(Dimens.spaceSm))
            Text(
                text = formatTimestamp(entry.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

private fun buildGreeting(name: String): String {
    val hour = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val timeOfDay = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
    return if (name.isNotBlank()) "$timeOfDay, $name" else timeOfDay
}

private fun formatTimestamp(timestamp: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}"
}
