package com.morozione.psychologyhelper.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.ui.component.BreathingCircle
import com.morozione.psychologyhelper.ui.component.BreathingPhase
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.theme.Dimens
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class BreathingState(
    val phase: BreathingPhase = BreathingPhase.INHALE,
    val secondsRemaining: Int = BreathingPhase.INHALE.durationSeconds,
    val isRunning: Boolean = false
)

class BreathingExerciseScreenModel : ScreenModel {

    private val _state = MutableStateFlow(BreathingState())
    val state: StateFlow<BreathingState> = _state.asStateFlow()

    private var job: Job? = null

    fun toggle() {
        if (_state.value.isRunning) stop() else start()
    }

    private fun start() {
        reduce { copy(isRunning = true) }
        job = screenModelScope.launch {
            while (isActive) {
                BreathingPhase.entries.forEach { phase ->
                    reduce { copy(phase = phase, secondsRemaining = phase.durationSeconds) }
                    repeat(phase.durationSeconds) {
                        if (!isActive) return@launch
                        delay(1000)
                        reduce { copy(secondsRemaining = (_state.value.secondsRemaining - 1).coerceAtLeast(0)) }
                    }
                }
            }
        }
    }

    private fun stop() {
        job?.cancel()
        job = null
        reduce { copy(isRunning = false, phase = BreathingPhase.INHALE, secondsRemaining = BreathingPhase.INHALE.durationSeconds) }
    }

    override fun onDispose() {
        job?.cancel()
        super.onDispose()
    }

    private fun reduce(reducer: BreathingState.() -> BreathingState) {
        _state.update { it.reducer() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class BreathingExerciseScreen(private val title: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<BreathingExerciseScreenModel>()
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
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
                    .padding(Dimens.spaceXxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BreathingCircle(
                    phase = state.phase,
                    progress = 0f
                )

                Spacer(Modifier.height(Dimens.space3xl))

                Text(
                    text = "${state.secondsRemaining}s",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(Dimens.spaceLg))

                Text(
                    text = state.phase.instruction,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(Dimens.space4xl))

                PsychologyButton(
                    text = if (state.isRunning) "Stop" else "Start",
                    onClick = { screenModel.toggle() }
                )
            }
        }
    }
}
