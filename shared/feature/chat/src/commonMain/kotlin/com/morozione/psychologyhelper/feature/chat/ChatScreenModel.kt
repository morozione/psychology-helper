package com.morozione.psychologyhelper.feature.chat

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.morozione.psychologyhelper.domain.entity.ChatMessage
import com.morozione.psychologyhelper.domain.repository.ChatRepository
import com.morozione.psychologyhelper.domain.usecase.auth.GetCurrentUserUseCase
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

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val userId: String = ""
)

sealed class ChatIntent {
    object LoadHistory : ChatIntent()
    data class UpdateInput(val text: String) : ChatIntent()
    object SendMessage : ChatIntent()
    object ClearHistory : ChatIntent()
    object Retry : ChatIntent()
}

sealed class ChatEffect {
    object ScrollToBottom : ChatEffect()
    data class ShowError(val message: String) : ChatEffect()
}

class ChatScreenModel(
    private val chatRepository: ChatRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ChatEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<ChatEffect> = _effects.asSharedFlow()

    init {
        screenModelScope.launch {
            getCurrentUserUseCase().filterNotNull().collectLatest { user ->
                reduce { copy(userId = user.id) }
                onIntent(ChatIntent.LoadHistory)
            }
        }
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.LoadHistory -> loadHistory()
            is ChatIntent.UpdateInput -> reduce { copy(inputText = intent.text) }
            is ChatIntent.SendMessage -> sendMessage()
            is ChatIntent.ClearHistory -> clearHistory()
            is ChatIntent.Retry -> {
                reduce { copy(error = null) }
                loadHistory()
            }
        }
    }

    private fun loadHistory() {
        val userId = _state.value.userId
        if (userId.isBlank()) return
        screenModelScope.launch {
            reduce { copy(isLoading = true, error = null) }
            chatRepository.getChatHistory(userId).collectLatest { messages ->
                reduce { copy(messages = messages, isLoading = false) }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return
        val userId = _state.value.userId
        if (userId.isBlank()) return

        screenModelScope.launch {
            reduce { copy(inputText = "", isSending = true, error = null) }
            val result = chatRepository.sendMessage(
                userId = userId,
                userMessage = text,
                history = _state.value.messages
            )
            result.fold(
                onSuccess = {
                    reduce { copy(isSending = false) }
                    _effects.emit(ChatEffect.ScrollToBottom)
                },
                onFailure = { e ->
                    val msg = e.message ?: "Failed to send message"
                    reduce { copy(isSending = false, error = msg) }
                    _effects.emit(ChatEffect.ShowError(msg))
                }
            )
        }
    }

    private fun clearHistory() {
        val userId = _state.value.userId
        if (userId.isBlank()) return
        screenModelScope.launch {
            chatRepository.clearHistory(userId)
        }
    }

    private fun reduce(reducer: ChatState.() -> ChatState) {
        _state.update { it.reducer() }
    }
}
