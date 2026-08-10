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
import com.morozione.psychologyhelper.feature.auth.LoginScreen
import com.morozione.psychologyhelper.feature.home.HomeScreen
import com.morozione.psychologyhelper.ui.theme.PsychologyTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    PsychologyTheme {
        val authRepository: AuthRepository = koinInject()

        // null = determining, true = authenticated, false = unauthenticated
        var authState by remember { mutableStateOf<Boolean?>(null) }

        LaunchedEffect(Unit) {
            authRepository.currentUser.collect { user: User? ->
                authState = user != null
            }
        }

        // Use key so the Navigator is fully recreated when auth state changes,
        // giving a clean navigation stack for each auth context.
        key(authState) {
            when (authState) {
                true -> Navigator(HomeScreen())
                false -> Navigator(LoginScreen())
                null -> Unit // Auth state is still loading; show nothing (splash could go here)
            }
        }
    }
}
