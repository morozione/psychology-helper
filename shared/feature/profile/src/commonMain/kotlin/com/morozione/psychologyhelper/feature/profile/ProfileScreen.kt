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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyCard
import com.morozione.psychologyhelper.ui.component.SectionTitle
import com.morozione.psychologyhelper.ui.theme.Dimens

@Composable
fun Screen.ProfileScreenContent(user: User?) {
    val screenModel = koinScreenModel<ProfileScreenModel>()
    val state by screenModel.state.collectAsState()

    LaunchedEffect(user) {
        screenModel.onIntent(ProfileIntent.Initialize(user))
    }

    // App.kt observes auth state changes and navigates to LoginScreen after logout automatically.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.spaceLg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(Dimens.space3xl))

        // Avatar with initials (photo upload is a future native feature)
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
            Text(
                text = initials,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
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
            Text(text = emoji, fontSize = 28.sp)
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
