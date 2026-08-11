package com.morozione.psychologyhelper.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import coil3.compose.AsyncImage
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyCard
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.util.rememberImagePickerLauncher

@Composable
fun Screen.ProfileScreenContent(user: User?) {
    val screenModel = koinScreenModel<ProfileScreenModel>()
    val state by screenModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user) {
        screenModel.onIntent(ProfileIntent.Initialize(user))
    }

    LaunchedEffect(Unit) {
        screenModel.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    val pickImage = rememberImagePickerLauncher { bytes ->
        screenModel.onIntent(ProfileIntent.UploadPhoto(bytes))
    }

    // App.kt observes auth state changes and navigates to LoginScreen after logout automatically.

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.space3xl))

            val initials = user?.displayName
                ?.split(" ")
                ?.take(2)
                ?.mapNotNull { it.firstOrNull()?.uppercase() }
                ?.joinToString("") ?: "?"

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(Dimens.avatarLg)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                if (state.photoUrl != null) {
                    AsyncImage(
                        model = state.photoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (state.isUploadingPhoto) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
            PsychologyButton(
                text = "Change photo",
                onClick = pickImage,
                isLoading = state.isUploadingPhoto,
                modifier = Modifier.padding(top = Dimens.spaceSm)
            )
            Spacer(Modifier.height(Dimens.spaceLg))

            Text(
                text = user?.displayName ?: "User",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(Dimens.space3xl))

            SectionTitle(
                text = "My Stats",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(Dimens.spaceSm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMd)
            ) {
                StatCard(
                    label = "Mood Entries",
                    value = state.moodEntryCount.toString(),
                    emoji = "😊",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Journal Entries",
                    value = state.journalEntryCount.toString(),
                    emoji = "📔",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(Dimens.space4xl))

            PsychologyButton(
                text = "Sign Out",
                onClick = { screenModel.onIntent(ProfileIntent.Logout) },
                isLoading = state.isLoggingOut
            )

            Spacer(Modifier.height(Dimens.space3xl))
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    PsychologyCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(Dimens.spaceSm))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
