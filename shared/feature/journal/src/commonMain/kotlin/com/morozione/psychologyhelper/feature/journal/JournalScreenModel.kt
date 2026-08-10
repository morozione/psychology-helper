package com.morozione.psychologyhelper.feature.journal

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.domain.usecase.journal.DeleteJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class JournalUiState(
    val entries: List<JournalEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class JournalScreenModel(
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(JournalUiState())
    val state: StateFlow<JournalUiState> = _state.asStateFlow()

    private var currentUserId: String = ""

    fun initialize(userId: String) {
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getJournalEntriesUseCase(userId).collectLatest { entries ->
                _state.value = _state.value.copy(entries = entries, isLoading = false)
            }
        }
    }

    fun deleteEntry(entryId: String) {
        screenModelScope.launch {
            val compositeId = "$currentUserId::$entryId"
            deleteJournalEntryUseCase(compositeId)
        }
    }
}
