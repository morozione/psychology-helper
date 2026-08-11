package com.morozione.psychologyhelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import com.morozione.psychologyhelper.domain.entity.User
import com.morozione.psychologyhelper.domain.repository.AuthRepository
import com.morozione.psychologyhelper.domain.util.AppPreferences
import com.morozione.psychologyhelper.feature.auth.LoginScreen
import com.morozione.psychologyhelper.feature.auth.OnboardingScreen
import com.morozione.psychologyhelper.feature.home.HomeScreen
import com.morozione.psychologyhelper.ui.theme.PsychologyTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    PsychologyTheme {
        val authRepository: AuthRepository = koinInject()
        val appPreferences: AppPreferences = koinInject()

        var authState by remember { mutableStateOf<Boolean?>(null) }

        LaunchedEffect(Unit) {
            authRepository.currentUser.collect { user: User? ->
                authState = user != null
            }
        }

        key(authState) {
            when (authState) {
                true -> Navigator(HomeScreen())
                false -> {
                    if (!appPreferences.isOnboardingComplete()) {
                        Navigator(OnboardingScreen())
                    } else {
                        Navigator(LoginScreen())
                    }
                }
                null -> Unit
            }
        }
    }
}
