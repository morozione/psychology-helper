package com.morozione.psychologyhelper.feature.journal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.domain.entity.JournalEntry
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyTextField
import com.morozione.psychologyhelper.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
class JournalEntryScreen(
    private val userId: String,
    private val existingEntry: JournalEntry? = null
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<JournalEntryScreenModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.onIntent(JournalEntryIntent.Initialize(userId, existingEntry))
            screenModel.effects.collect { effect ->
                when (effect) {
                    is JournalEntryEffect.SavedSuccessfully -> navigator.pop()
                    is JournalEntryEffect.ShowError -> { /* error shown inline */ }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = if (existingEntry != null) "Edit Entry" else "New Entry")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = Dimens.spaceLg, vertical = Dimens.spaceSm)
            ) {
                PsychologyTextField(
                    value = state.title,
                    onValueChange = { screenModel.onIntent(JournalEntryIntent.UpdateTitle(it)) },
                    label = "Title",
                    placeholder = "Entry title..."
                )
                Spacer(Modifier.height(Dimens.spaceLg))
                PsychologyTextField(
                    value = state.content,
                    onValueChange = { screenModel.onIntent(JournalEntryIntent.UpdateContent(it)) },
                    label = "Content",
                    placeholder = "Write your thoughts here...",
                    singleLine = false,
                    minLines = 10,
                    maxLines = 50,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.error != null) {
                    Spacer(Modifier.height(Dimens.spaceXs))
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.height(Dimens.spaceLg))
                PsychologyButton(
                    text = if (existingEntry != null) "Update Entry" else "Save Entry",
                    onClick = { screenModel.onIntent(JournalEntryIntent.Save) },
                    isLoading = state.isSaving
                )
                Spacer(Modifier.height(Dimens.spaceLg))
            }
        }
    }
}
