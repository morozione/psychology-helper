package com.morozione.psychologyhelper.feature.mood

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.usecase.mood.AddMoodEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

data class MoodState(
    val entries: List<MoodEntry> = emptyList(),
    val selectedMood: Mood? = null,
    val note: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

sealed class MoodIntent {
    data class Initialize(val userId: String) : MoodIntent()
    data class SelectMood(val mood: Mood) : MoodIntent()
    data class UpdateNote(val note: String) : MoodIntent()
    object Save : MoodIntent()
    object Retry : MoodIntent()
}

sealed class MoodEffect {
    object SavedSuccessfully : MoodEffect()
    data class ShowError(val message: String) : MoodEffect()
}

class MoodScreenModel(
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val addMoodEntryUseCase: AddMoodEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(MoodState())
    val state: StateFlow<MoodState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MoodEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<MoodEffect> = _effects.asSharedFlow()

    private var currentUserId: String = ""

    fun onIntent(intent: MoodIntent) {
        when (intent) {
            is MoodIntent.Initialize -> initialize(intent.userId)
            is MoodIntent.SelectMood -> reduce { copy(selectedMood = intent.mood, error = null) }
            is MoodIntent.UpdateNote -> reduce { copy(note = intent.note) }
            is MoodIntent.Save -> saveMoodEntry()
            is MoodIntent.Retry -> reduce { copy(error = null) }
        }
    }

    private fun initialize(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(entries = entries) }
            }
        }
    }

    private fun saveMoodEntry() {
        val mood = _state.value.selectedMood ?: run {
            val msg = "Please select a mood"
            reduce { copy(error = msg) }
            return
        }
        if (currentUserId.isBlank()) return

        screenModelScope.launch {
            reduce { copy(isSaving = true, error = null) }
            val entry = MoodEntry(
                id = generateId(),
                userId = currentUserId,
                mood = mood,
                note = _state.value.note.trim(),
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val result = addMoodEntryUseCase(entry)
            if (result.isSuccess) {
                reduce { copy(isSaving = false, saveSuccess = true, selectedMood = null, note = "") }
                _effects.emit(MoodEffect.SavedSuccessfully)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to save mood"
                reduce { copy(isSaving = false, error = msg) }
                _effects.emit(MoodEffect.ShowError(msg))
            }
        }
    }

    private fun reduce(reducer: MoodState.() -> MoodState) {
        _state.update { it.reducer() }
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
