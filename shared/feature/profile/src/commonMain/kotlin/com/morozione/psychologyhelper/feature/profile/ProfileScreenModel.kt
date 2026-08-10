package com.morozione.psychologyhelper.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.LogoutUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProfileUiState(
    val moodEntryCount: Int = 0,
    val journalEntryCount: Int = 0,
    val isLoggingOut: Boolean = false
)

class ProfileScreenModel(
    private val logoutUseCase: LogoutUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private var currentUserId: String = ""

    fun initialize(user: User?) {
        val userId = user?.id ?: return
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                _state.value = _state.value.copy(moodEntryCount = entries.size)
            }
        }
        screenModelScope.launch {
            getJournalEntriesUseCase(userId).collectLatest { entries ->
                _state.value = _state.value.copy(journalEntryCount = entries.size)
            }
        }
    }

    fun logout() {
        screenModelScope.launch {
            _state.value = _state.value.copy(isLoggingOut = true)
            logoutUseCase()
            // App.kt observes auth state change and handles navigation to LoginScreen
            _state.value = _state.value.copy(isLoggingOut = false)
        }
    }
}
