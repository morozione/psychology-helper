package com.morozione.psychologyhelper.feature.journal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.usecase.journal.AddJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.UpdateJournalEntryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.random.Random

data class JournalEntryUiState(
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class JournalEntryScreenModel(
    private val addJournalEntryUseCase: AddJournalEntryUseCase,
    private val updateJournalEntryUseCase: UpdateJournalEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(JournalEntryUiState())
    val state: StateFlow<JournalEntryUiState> = _state.asStateFlow()

    private var userId: String = ""
    private var existingEntry: JournalEntry? = null

    fun initialize(userId: String, entry: JournalEntry? = null) {
        this.userId = userId
        this.existingEntry = entry
        if (entry != null) {
            _state.value = _state.value.copy(
                title = entry.title,
                content = entry.content
            )
        }
    }

    fun onTitleChanged(value: String) {
        _state.value = _state.value.copy(title = value)
    }

    fun onContentChanged(value: String) {
        _state.value = _state.value.copy(content = value)
    }

    fun save() {
        val title = _state.value.title.trim()
        val content = _state.value.content.trim()
        if (title.isBlank()) {
            _state.value = _state.value.copy(error = "Title cannot be empty")
            return
        }
        if (content.isBlank()) {
            _state.value = _state.value.copy(error = "Content cannot be empty")
            return
        }
        screenModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            val existing = existingEntry
            val result = if (existing != null) {
                updateJournalEntryUseCase(
                    existing.copy(title = title, content = content)
                )
            } else {
                addJournalEntryUseCase(
                    JournalEntry(
                        id = generateId(),
                        userId = userId,
                        title = title,
                        content = content,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                )
            }
            _state.value = if (result.isSuccess) {
                _state.value.copy(isSaving = false, saveSuccess = true)
            } else {
                _state.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to save entry"
                )
            }
        }
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
