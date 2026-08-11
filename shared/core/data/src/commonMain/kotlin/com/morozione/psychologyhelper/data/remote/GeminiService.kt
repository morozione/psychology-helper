package com.morozione.psychologyhelper.data.remote

import com.morozione.psychologyhelper.domain.entity.ChatMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(val contents: List<GeminiContent>)

@Serializable
data class GeminiContent(val role: String, val parts: List<GeminiPart>)

@Serializable
data class GeminiPart(val text: String)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(val content: GeminiContent)

@Serializable
data class GeminiError(val message: String)

private const val GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

class GeminiService(private val httpClient: HttpClient, private val apiKey: String) {

    suspend fun sendMessage(history: List<ChatMessage>, systemPrompt: String): Result<String> =
        runCatching {
            val contents = buildList {
                add(GeminiContent(role = "user", parts = listOf(GeminiPart(systemPrompt))))
                add(GeminiContent(role = "model", parts = listOf(GeminiPart("Understood. I'm here to listen and support you."))))
                addAll(history.map { msg ->
                    GeminiContent(
                        role = if (msg.isFromUser) "user" else "model",
                        parts = listOf(GeminiPart(msg.content))
                    )
                })
            }
            callGemini(contents)
        }

    suspend fun generateInsight(prompt: String): Result<String> = runCatching {
        val contents = listOf(
            GeminiContent(role = "user", parts = listOf(GeminiPart(prompt)))
        )
        callGemini(contents)
    }

    private suspend fun callGemini(contents: List<GeminiContent>): String {
        val response: GeminiResponse = httpClient.post(GEMINI_URL) {
            parameter("key", apiKey)
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(contents))
        }.body()
        response.error?.let { error(it.message) }
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: error("Empty response from Gemini")
    }
}
