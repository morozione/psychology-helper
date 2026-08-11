package com.morozione.psychologyhelper.feature.auth

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.domain.util.AppPreferences
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.theme.Dimens
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private data class OnboardingPage(
    val emoji: String,
    val title: String,
    val subtitle: String
)

private val pages = listOf(
    OnboardingPage(
        emoji = "🧠",
        title = "Your mental wellness companion",
        subtitle = "Track your mood, journal your thoughts, and get AI-powered insights."
    ),
    OnboardingPage(
        emoji = "📊",
        title = "Understand your patterns",
        subtitle = "See your emotional trends over time and discover what affects your wellbeing."
    ),
    OnboardingPage(
        emoji = "🌿",
        title = "Tools that actually help",
        subtitle = "Breathing exercises, grounding techniques, and a supportive AI chat."
    )
)

class OnboardingScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val appPreferences: AppPreferences = koinInject()
        val pagerState = rememberPagerState(pageCount = { pages.size })
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spaceXxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            Row(
                modifier = Modifier.padding(vertical = Dimens.spaceLg),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(Dimens.spaceLg))

            PsychologyButton(
                text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        appPreferences.setOnboardingComplete()
                        navigator.replaceAll(listOf(LoginScreen()))
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )

            Spacer(Modifier.height(Dimens.spaceLg))
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spaceXxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(page.emoji, style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(Dimens.space3xl))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(Dimens.spaceLg))
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
