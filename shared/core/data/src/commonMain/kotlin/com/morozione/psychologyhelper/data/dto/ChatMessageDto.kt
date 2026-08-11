package com.morozione.psychologyhelper.data.dto

import com.morozione.psychologyhelper.domain.entity.ChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: String = "",
    val content: String = "",
    val isFromUser: Boolean = true,
    val timestamp: Long = 0L
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        content = content,
        isFromUser = isFromUser,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(msg: ChatMessage): ChatMessageDto = ChatMessageDto(
            id = msg.id,
            content = msg.content,
            isFromUser = msg.isFromUser,
            timestamp = msg.timestamp
        )
    }
}
