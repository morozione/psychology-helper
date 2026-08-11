package com.morozione.psychologyhelper.feature.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.GetCurrentUserUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.AddMoodEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Clock

data class HomeState(
    val user: User? = null,
    val recentMoodEntries: List<MoodEntry> = emptyList(),
    val streakDays: Int = 0,
    val hasTodayEntry: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedTab: BottomTab = BottomTab.HOME
)

sealed class HomeIntent {
    object LoadData : HomeIntent()
    object Retry : HomeIntent()
    data class LogMood(val mood: Mood) : HomeIntent()
    data class SelectTab(val tab: BottomTab) : HomeIntent()
}

sealed class HomeEffect {
    data class ShowError(val message: String) : HomeEffect()
}

class HomeScreenModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val addMoodEntryUseCase: AddMoodEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<HomeEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<HomeEffect> = _effects.asSharedFlow()

    init {
        onIntent(HomeIntent.LoadData)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadData -> loadData()
            is HomeIntent.Retry -> {
                reduce { copy(error = null, isLoading = true) }
                loadData()
            }
            is HomeIntent.LogMood -> logMood(intent.mood)
            is HomeIntent.SelectTab -> reduce { copy(selectedTab = intent.tab) }
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            getCurrentUserUseCase().filterNotNull().collectLatest { user ->
                reduce { copy(user = user, isLoading = false) }
                getMoodEntriesUseCase(user.id).collectLatest { entries ->
                    val tz = TimeZone.currentSystemDefault()
                    val today = Clock.System.now().toLocalDateTime(tz).date
                    val hasTodayEntry = entries.any { entry ->
                        Instant.fromEpochMilliseconds(entry.timestamp).toLocalDateTime(tz).date == today
                    }
                    val streak = calculateStreak(entries, today, tz)
                    reduce {
                        copy(
                            recentMoodEntries = entries.take(3),
                            streakDays = streak,
                            hasTodayEntry = hasTodayEntry
                        )
                    }
                }
            }
        }
    }

    private fun logMood(mood: Mood) {
        val userId = _state.value.user?.id ?: return
        screenModelScope.launch {
            val entry = MoodEntry(
                id = generateId(),
                userId = userId,
                mood = mood,
                note = "",
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            addMoodEntryUseCase(entry).onFailure { e ->
                _effects.emit(HomeEffect.ShowError(e.message ?: "Failed to log mood"))
            }
        }
    }

    private fun calculateStreak(entries: List<MoodEntry>, today: LocalDate, tz: TimeZone): Int {
        if (entries.isEmpty()) return 0
        val entryDates = entries.map {
            Instant.fromEpochMilliseconds(it.timestamp).toLocalDateTime(tz).date
        }.toSet()
        var streak = 0
        var checkDate = today
        while (entryDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minus(1, DateTimeUnit.DAY)
        }
        return streak
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }

    private fun reduce(reducer: HomeState.() -> HomeState) {
        _state.update { it.reducer() }
    }
}
