package com.morozione.psychologyhelper.feature.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.ui.component.Exercise
import com.morozione.psychologyhelper.ui.component.ExerciseCard
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.theme.MoodGood
import com.morozione.psychologyhelper.ui.theme.MoodGreat
import com.morozione.psychologyhelper.ui.theme.MoodOkay
import com.morozione.psychologyhelper.ui.theme.PrimaryGreen
import com.morozione.psychologyhelper.ui.theme.SecondaryTeal

private val breathingExercises = setOf("4-7-8 Breathing", "Box Breathing")

private val exercises = listOf(
    Exercise("4-7-8 Breathing", "Calm anxiety fast", "🫁", 5, MoodGood),
    Exercise("Box Breathing", "Balance your nervous system", "⬜", 4, SecondaryTeal),
    Exercise("5-4-3-2-1 Grounding", "Reconnect with the present", "🌿", 3, PrimaryGreen),
    Exercise("Body Scan", "Release tension head to toe", "🧘", 7, MoodOkay),
    Exercise("Gratitude List", "Shift focus to the positive", "🙏", 3, MoodGreat)
)

@Composable
fun ToolsScreenContent() {
    val navigator = LocalNavigator.currentOrThrow
    var dialogExercise by remember { mutableStateOf<Exercise?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.spaceLg)
    ) {
        item { Spacer(Modifier.height(Dimens.spaceXxl)) }
        item { SectionTitle("Exercises & Tools") }
        item { Spacer(Modifier.height(Dimens.spaceSm)) }

        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onClick = {
                    if (exercise.title in breathingExercises) {
                        navigator.push(BreathingExerciseScreen(exercise.title))
                    } else {
                        dialogExercise = exercise
                    }
                }
            )
            Spacer(Modifier.height(Dimens.spaceSm))
        }

        item { Spacer(Modifier.height(Dimens.spaceLg)) }
    }

    dialogExercise?.let { exercise ->
        AlertDialog(
            onDismissRequest = { dialogExercise = null },
            title = { Text("${exercise.emoji} ${exercise.title}") },
            text = {
                Text(
                    "${exercise.description}\n\nTake ${exercise.durationMinutes} minutes to practice this technique. Find a quiet space, close your eyes, and follow the exercise at your own pace.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { dialogExercise = null }) {
                    Text("Got it")
                }
            }
        )
    }
}
