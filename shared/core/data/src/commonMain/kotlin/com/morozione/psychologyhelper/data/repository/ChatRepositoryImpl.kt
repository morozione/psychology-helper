package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.data.dto.ChatMessageDto
import com.morozione.psychologyhelper.data.remote.GeminiService
import com.morozione.psychologyhelper.domain.entity.ChatMessage
import com.morozione.psychologyhelper.domain.repository.ChatRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.random.Random

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val geminiService: GeminiService
) : ChatRepository {

    private fun chatCollection(userId: String) =
        firestore.collection("users").document(userId).collection("chat")

    override fun getChatHistory(userId: String): Flow<List<ChatMessage>> =
        chatCollection(userId).snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                runCatching { doc.data<ChatMessageDto>().toDomain() }.getOrNull()
            }.sortedBy { it.timestamp }
        }

    override suspend fun sendMessage(
        userId: String,
        userMessage: String,
        history: List<ChatMessage>
    ): Result<ChatMessage> = runCatching {
        val userTimestamp = Clock.System.now().toEpochMilliseconds()
        val userMsgId = generateId()
        val userChatMessage = ChatMessage(
            id = userMsgId,
            content = userMessage,
            isFromUser = true,
            timestamp = userTimestamp
        )

        // Save user message to Firestore
        chatCollection(userId).document(userMsgId).set(ChatMessageDto.fromDomain(userChatMessage))

        // Call Gemini with history + new user message
        val fullHistory = history + userChatMessage
        val aiText = geminiService.sendMessage(fullHistory).getOrThrow()

        val aiTimestamp = Clock.System.now().toEpochMilliseconds()
        val aiMsgId = generateId()
        val aiChatMessage = ChatMessage(
            id = aiMsgId,
            content = aiText,
            isFromUser = false,
            timestamp = aiTimestamp
        )

        // Save AI response to Firestore
        chatCollection(userId).document(aiMsgId).set(ChatMessageDto.fromDomain(aiChatMessage))

        aiChatMessage
    }

    override suspend fun clearHistory(userId: String): Result<Unit> = runCatching {
        val snapshot = chatCollection(userId).get()
        snapshot.documents.forEach { doc ->
            chatCollection(userId).document(doc.id).delete()
        }
    }

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
