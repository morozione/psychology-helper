package com.morozione.psychologyhelper.feature.mood

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.Mood
import com.morozione.psychologyhelper.domain.entity.MoodEntry
import com.morozione.psychologyhelper.domain.usecase.mood.AddMoodEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

data class MoodUiState(
    val entries: List<MoodEntry> = emptyList(),
    val selectedMood: Mood? = null,
    val note: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class MoodScreenModel(
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val addMoodEntryUseCase: AddMoodEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(MoodUiState())
    val state: StateFlow<MoodUiState> = _state.asStateFlow()

    private var currentUserId: String = ""

    fun initialize(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                _state.value = _state.value.copy(entries = entries)
            }
        }
    }

    fun selectMood(mood: Mood) {
        _state.value = _state.value.copy(selectedMood = mood)
    }

    fun onNoteChanged(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun saveMoodEntry() {
        val mood = _state.value.selectedMood ?: run {
            _state.value = _state.value.copy(error = "Please select a mood")
            return
        }
        if (currentUserId.isBlank()) return

        screenModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            val entry = MoodEntry(
                id = generateId(),
                userId = currentUserId,
                mood = mood,
                note = _state.value.note.trim(),
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            val result = addMoodEntryUseCase(entry)
            _state.value = if (result.isSuccess) {
                _state.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    selectedMood = null,
                    note = ""
                )
            } else {
                _state.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to save mood"
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _state.value = _state.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
