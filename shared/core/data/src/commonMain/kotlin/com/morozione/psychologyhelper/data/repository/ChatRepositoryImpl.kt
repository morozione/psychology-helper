package com.morozione.psychologyhelper.data.repository

import com.morozione.psychologyhelper.data.dto.ChatMessageDto
import com.morozione.psychologyhelper.data.remote.GeminiService
import com.morozione.psychologyhelper.domain.entity.ChatMessage
import com.morozione.psychologyhelper.domain.repository.ChatRepository
import com.morozione.psychologyhelper.domain.repository.MoodRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val geminiService: GeminiService,
    private val moodRepository: MoodRepository
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

        chatCollection(userId).document(userMsgId).set(ChatMessageDto.fromDomain(userChatMessage))

        val moodSummary = getMoodContext(userId)
        val systemPrompt = """
            You are a compassionate psychological assistant.
            Context about the user: $moodSummary
            Be empathetic and reference their recent emotional state when relevant.
            Always recommend professional help for serious concerns.
        """.trimIndent()

        val fullHistory = history + userChatMessage
        val aiText = geminiService.sendMessage(fullHistory, systemPrompt).getOrThrow()

        val aiTimestamp = Clock.System.now().toEpochMilliseconds()
        val aiMsgId = generateId()
        val aiChatMessage = ChatMessage(
            id = aiMsgId,
            content = aiText,
            isFromUser = false,
            timestamp = aiTimestamp
        )

        chatCollection(userId).document(aiMsgId).set(ChatMessageDto.fromDomain(aiChatMessage))

        aiChatMessage
    }

    override suspend fun clearHistory(userId: String): Result<Unit> = runCatching {
        val snapshot = chatCollection(userId).get()
        snapshot.documents.forEach { doc ->
            chatCollection(userId).document(doc.id).delete()
        }
    }

    override suspend fun getMoodContext(userId: String): String {
        val recentEntries = runCatching {
            moodRepository.getMoodEntries(userId).first().take(5)
        }.getOrElse { emptyList() }

        if (recentEntries.isEmpty()) return "No recent mood data available."

        val tz = TimeZone.currentSystemDefault()
        val formatted = recentEntries.joinToString(", ") { entry ->
            val dt = Instant.fromEpochMilliseconds(entry.timestamp).toLocalDateTime(tz)
            val month = dt.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
            "${entry.mood.label} on $month ${dt.dayOfMonth}"
        }
        return "Recent moods: [$formatted]"
    }

    override suspend fun generateInsight(prompt: String): Result<String> =
        geminiService.generateInsight(prompt)

    private fun generateId(): String = buildString {
        repeat(20) { append(Random.nextInt(16).toString(16)) }
    }
}
