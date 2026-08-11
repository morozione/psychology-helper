package com.morozione.psychologyhelper.feature.chat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.morozione.psychologyhelper.domain.entity.ChatMessage
import com.morozione.psychologyhelper.ui.component.ErrorContent
import com.morozione.psychologyhelper.ui.component.LoadingContent
import com.morozione.psychologyhelper.ui.theme.Dimens
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChatScreen : Screen {
    @Composable
    override fun Content() {
        ChatScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.ChatScreenContent() {
    val screenModel = koinScreenModel<ChatScreenModel>()
    val state by screenModel.state.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        screenModel.effects.collect { effect ->
            when (effect) {
                is ChatEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.size - 1)
                    }
                }
                is ChatEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧠", fontSize = 20.sp)
                        Spacer(Modifier.width(Dimens.spaceSm))
                        Column {
                            Text(
                                text = "Psychology Assistant",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Here to listen",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { screenModel.onIntent(ChatIntent.ClearHistory) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear history",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                text = state.inputText,
                onTextChange = { screenModel.onIntent(ChatIntent.UpdateInput(it)) },
                onSend = { screenModel.onIntent(ChatIntent.SendMessage) },
                isSending = state.isSending,
                modifier = Modifier.imePadding()
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingContent()
            state.error != null && state.messages.isEmpty() -> ErrorContent(
                message = state.error!!,
                onRetry = { screenModel.onIntent(ChatIntent.Retry) }
            )
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = Dimens.spaceLg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
                ) {
                    if (state.messages.isEmpty() && !state.isLoading) {
                        item {
                            Spacer(Modifier.height(Dimens.space3xl))
                            GreetingBubble()
                        }
                    } else {
                        items(state.messages, key = { it.id }) { message ->
                            MessageBubble(message = message)
                        }
                    }

                    if (state.isSending) {
                        item { TypingIndicator() }
                    }

                    item { Spacer(Modifier.height(Dimens.spaceSm)) }
                }
            }
        }
    }
}

@Composable
private fun GreetingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        AiAvatar()
        Spacer(Modifier.width(Dimens.spaceSm))
        Column(modifier = Modifier.widthIn(max = 280.dp)) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = Dimens.radiusLg,
                            topEnd = Dimens.radiusLg,
                            bottomEnd = Dimens.radiusLg,
                            bottomStart = Dimens.radiusSm
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm)
            ) {
                Text(
                    text = "Hi! I'm your psychology assistant. How are you feeling today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    if (message.isFromUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.widthIn(max = 280.dp),
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = Dimens.radiusLg,
                                topEnd = Dimens.radiusLg,
                                bottomEnd = Dimens.radiusSm,
                                bottomStart = Dimens.radiusLg
                            )
                        )
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(Dimens.spaceXxs))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            AiAvatar()
            Spacer(Modifier.width(Dimens.spaceSm))
            Column(modifier = Modifier.widthIn(max = 280.dp)) {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = Dimens.radiusLg,
                                topEnd = Dimens.radiusLg,
                                bottomEnd = Dimens.radiusLg,
                                bottomStart = Dimens.radiusSm
                            )
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.height(Dimens.spaceXxs))
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun AiAvatar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(Dimens.avatarSm)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Text("🧠", fontSize = 16.sp)
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "typing_alpha"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AiAvatar()
        Spacer(Modifier.width(Dimens.spaceSm))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.radiusLg))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = Dimens.spaceMd, vertical = Dimens.spaceSm)
        ) {
            Text(
                text = "Typing...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceSm),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Type a message...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            shape = RoundedCornerShape(Dimens.radiusLg),
            maxLines = 4
        )
        Spacer(Modifier.width(Dimens.spaceSm))
        IconButton(
            onClick = onSend,
            enabled = text.isNotBlank() && !isSending,
            modifier = Modifier
                .size(Dimens.iconXl)
                .clip(CircleShape)
                .background(
                    if (text.isNotBlank() && !isSending)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send message",
                tint = Color.White,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dt = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
}
