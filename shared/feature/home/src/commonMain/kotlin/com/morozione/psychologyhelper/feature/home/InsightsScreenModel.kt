package com.morozione.psychologyhelper.feature.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.repository.ChatRepository
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class InsightsState(
    val weekEntries: List<MoodEntry> = emptyList(),
    val weeklyInsight: String = "",
    val isLoadingInsight: Boolean = false,
    val totalEntries: Int = 0,
    val bestMoodLabel: String = "-",
    val isLoading: Boolean = true
)

sealed class InsightsIntent {
    data class Load(val userId: String) : InsightsIntent()
}

class InsightsScreenModel(
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val chatRepository: ChatRepository
) : ScreenModel {

    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()

    fun onIntent(intent: InsightsIntent) {
        when (intent) {
            is InsightsIntent.Load -> load(intent.userId)
        }
    }

    private fun load(userId: String) {
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                val tz = TimeZone.currentSystemDefault()
                val now = Clock.System.now()
                val sevenDaysAgo = now.minus(7, DateTimeUnit.DAY, tz)

                val weekEntries = entries.filter { entry ->
                    Instant.fromEpochMilliseconds(entry.timestamp) >= sevenDaysAgo
                }

                val bestMood = entries.minByOrNull { it.mood.ordinal }
                reduce {
                    copy(
                        weekEntries = weekEntries,
                        totalEntries = entries.size,
                        bestMoodLabel = bestMood?.mood?.label ?: "-",
                        isLoading = false
                    )
                }

                if (weekEntries.isNotEmpty()) {
                    reduce { copy(isLoadingInsight = true) }
                    val moodList = weekEntries.joinToString(", ") { entry ->
                        val dt = Instant.fromEpochMilliseconds(entry.timestamp).toLocalDateTime(tz)
                        val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                        "${entry.mood.label} on $month ${dt.dayOfMonth}"
                    }
                    val prompt = "The user's mood entries this week: $moodList. Give a 2-sentence empathetic insight about their emotional patterns."
                    chatRepository.generateInsight(prompt).fold(
                        onSuccess = { insight -> reduce { copy(weeklyInsight = insight, isLoadingInsight = false) } },
                        onFailure = { reduce { copy(isLoadingInsight = false) } }
                    )
                }
            }
        }
    }

    private fun reduce(reducer: InsightsState.() -> InsightsState) {
        _state.update { it.reducer() }
    }
}
