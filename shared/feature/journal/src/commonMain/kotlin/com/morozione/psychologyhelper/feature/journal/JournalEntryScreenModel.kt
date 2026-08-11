package com.morozione.psychologyhelper.feature.journal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.usecase.journal.AddJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.UpdateJournalEntryUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

data class JournalEntryState(
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class JournalEntryIntent {
    data class Initialize(val userId: String, val entry: JournalEntry? = null) : JournalEntryIntent()
    data class UpdateTitle(val value: String) : JournalEntryIntent()
    data class UpdateContent(val value: String) : JournalEntryIntent()
    object Save : JournalEntryIntent()
    object Retry : JournalEntryIntent()
}

sealed class JournalEntryEffect {
    object SavedSuccessfully : JournalEntryEffect()
    data class ShowError(val message: String) : JournalEntryEffect()
}

class JournalEntryScreenModel(
    private val addJournalEntryUseCase: AddJournalEntryUseCase,
    private val updateJournalEntryUseCase: UpdateJournalEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(JournalEntryState())
    val state: StateFlow<JournalEntryState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<JournalEntryEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<JournalEntryEffect> = _effects.asSharedFlow()

    private var userId: String = ""
    private var existingEntry: JournalEntry? = null

    fun onIntent(intent: JournalEntryIntent) {
        when (intent) {
            is JournalEntryIntent.Initialize -> initialize(intent.userId, intent.entry)
            is JournalEntryIntent.UpdateTitle -> reduce { copy(title = intent.value) }
            is JournalEntryIntent.UpdateContent -> reduce { copy(content = intent.value) }
            is JournalEntryIntent.Save -> save()
            is JournalEntryIntent.Retry -> reduce { copy(error = null) }
        }
    }

    private fun initialize(userId: String, entry: JournalEntry?) {
        this.userId = userId
        this.existingEntry = entry
        if (entry != null) {
            reduce { copy(title = entry.title, content = entry.content) }
        }
    }

    private fun save() {
        val title = _state.value.title.trim()
        val content = _state.value.content.trim()
        if (title.isBlank()) {
            val msg = "Title cannot be empty"
            reduce { copy(error = msg) }
            return
        }
        if (content.isBlank()) {
            val msg = "Content cannot be empty"
            reduce { copy(error = msg) }
            return
        }
        screenModelScope.launch {
            reduce { copy(isSaving = true, error = null) }
            val existing = existingEntry
            val result = if (existing != null) {
                updateJournalEntryUseCase(existing.copy(title = title, content = content))
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
            if (result.isSuccess) {
                reduce { copy(isSaving = false, saveSuccess = true) }
                _effects.emit(JournalEntryEffect.SavedSuccessfully)
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Failed to save entry"
                reduce { copy(isSaving = false, error = msg) }
                _effects.emit(JournalEntryEffect.ShowError(msg))
            }
        }
    }

    private fun reduce(reducer: JournalEntryState.() -> JournalEntryState) {
        _state.update { it.reducer() }
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
