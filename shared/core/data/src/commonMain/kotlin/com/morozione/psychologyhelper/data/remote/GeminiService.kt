package com.morozione.psychologyhelper.data.remote

import com.morozione.psychologyhelper.domain.entity.ChatMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
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
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent"

private const val MAX_RETRIES = 2
private val TRANSIENT_MARKERS = listOf("overloaded", "high demand", "unavailable", "resource_exhausted")

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
        var lastError: Throwable? = null
        repeat(MAX_RETRIES + 1) { attempt ->
            val response: GeminiResponse = httpClient.post(GEMINI_URL) {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(GeminiRequest(contents))
            }.body()

            val errorMessage = response.error?.message
            if (errorMessage == null) {
                return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: error("Empty response from Gemini")
            }

            val isTransient = TRANSIENT_MARKERS.any { errorMessage.contains(it, ignoreCase = true) }
            lastError = RuntimeException(errorMessage)
            if (!isTransient || attempt == MAX_RETRIES) {
                throw lastError as Throwable
            }
            delay(500L * (attempt + 1))
        }
        throw lastError ?: error("Empty response from Gemini")
    }
}
