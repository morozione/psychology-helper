package com.morozione.psychologyhelper.feature.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.usecase.auth.LogoutUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
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

data class ProfileState(
    val moodEntryCount: Int = 0,
    val journalEntryCount: Int = 0,
    val isLoggingOut: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ProfileIntent {
    data class Initialize(val user: User?) : ProfileIntent()
    object Logout : ProfileIntent()
    object Retry : ProfileIntent()
}

sealed class ProfileEffect {
    data class ShowError(val message: String) : ProfileEffect()
}

class ProfileScreenModel(
    private val logoutUseCase: LogoutUseCase,
    private val getMoodEntriesUseCase: GetMoodEntriesUseCase,
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ProfileEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ProfileEffect> = _effects.asSharedFlow()

    private var currentUserId: String = ""

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.Initialize -> initialize(intent.user)
            is ProfileIntent.Logout -> logout()
            is ProfileIntent.Retry -> reduce { copy(error = null) }
        }
    }

    private fun initialize(user: User?) {
        val userId = user?.id ?: return
        if (currentUserId == userId) return
        currentUserId = userId
        screenModelScope.launch {
            getMoodEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(moodEntryCount = entries.size) }
            }
        }
        screenModelScope.launch {
            getJournalEntriesUseCase(userId).collectLatest { entries ->
                reduce { copy(journalEntryCount = entries.size) }
            }
        }
    }

    private fun logout() {
        screenModelScope.launch {
            reduce { copy(isLoggingOut = true) }
            logoutUseCase()
            // App.kt observes auth state change and handles navigation to LoginScreen
            reduce { copy(isLoggingOut = false) }
        }
    }

    private fun reduce(reducer: ProfileState.() -> ProfileState) {
        _state.update { it.reducer() }
    }
}
