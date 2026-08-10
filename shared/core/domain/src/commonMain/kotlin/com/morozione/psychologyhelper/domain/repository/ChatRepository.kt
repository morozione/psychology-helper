package com.morozione.psychologyhelper.domain.repository

import com.morozione.psychologyhelper.domain.entity.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatHistory(userId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(
        userId: String,
        userMessage: String,
        history: List<ChatMessage>
    ): Result<ChatMessage>
    suspend fun clearHistory(userId: String): Result<Unit>
}
