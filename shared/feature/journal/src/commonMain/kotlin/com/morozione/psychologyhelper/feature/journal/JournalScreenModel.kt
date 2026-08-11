package com.morozione.psychologyhelper.feature.journal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.usecase.journal.DeleteJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JournalState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class JournalIntent {
    data class Initialize(val userId: String) : JournalIntent()
    data class DeleteEntry(val entryId: String) : JournalIntent()
    object Retry : JournalIntent()
}

sealed class JournalEffect {
    data class ShowError(val message: String) : JournalEffect()
}

class JournalScreenModel(
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<JournalEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<JournalEffect> = _effects.asSharedFlow()

    private var currentUserId: String = ""

    fun onIntent(intent: JournalIntent) {
        when (intent) {
            is JournalIntent.Initialize -> initialize(intent.userId)
            is JournalIntent.DeleteEntry -> deleteEntry(intent.entryId)
            is JournalIntent.Retry -> {
                reduce { copy(error = null, isLoading = true) }
                if (currentUserId.isNotBlank()) initialize(currentUserId)
            }
        }
    }

    private fun initialize(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getJournalEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(entries = entries, isLoading = false) }
            }
        }
    }

    private fun deleteEntry(entryId: String) {
        screenModelScope.launch {
            val compositeId = "$currentUserId::$entryId"
            deleteJournalEntryUseCase(compositeId)
        }
    }

    private fun reduce(reducer: JournalState.() -> JournalState) {
        _state.update { it.reducer() }
    }
}
