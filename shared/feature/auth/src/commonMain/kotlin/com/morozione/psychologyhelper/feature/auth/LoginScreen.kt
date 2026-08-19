package com.morozione.psychologyhelper.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyTextField
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.util.rememberGoogleSignInLauncher

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LoginScreenModel>()
        val state by screenModel.state.collectAsState()
        var passwordVisible by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }
        val launchGoogleSignIn = rememberGoogleSignInLauncher { idToken, error ->
            screenModel.onIntent(LoginIntent.GoogleSignInResult(idToken, error))
        }

        LaunchedEffect(Unit) {
            screenModel.effects.collect { effect ->
                when (effect) {
                    is LoginEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = Dimens.spaceXxl, vertical = Dimens.space3xl)
                ) {
                    Text(text = "🧠", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(Dimens.spaceSm))
                    Text(
                        text = "MindHelper",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Your mental wellness companion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(Dimens.space4xl))

                    PsychologyTextField(
                        value = state.email,
                        onValueChange = { screenModel.onIntent(LoginIntent.UpdateEmail(it)) },
                        label = "Email",
                        placeholder = "your@email.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(Dimens.spaceLg))

                    PsychologyTextField(
                        value = state.password,
                        onValueChange = { screenModel.onIntent(LoginIntent.UpdatePassword(it)) },
                        label = "Password",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(Dimens.spaceXxl))

                    PsychologyButton(
                        text = "Sign In",
                        onClick = { screenModel.onIntent(LoginIntent.Submit) },
                        isLoading = state.isLoading
                    )
                    Spacer(Modifier.height(Dimens.spaceLg))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            text = "  or  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(Dimens.spaceLg))

                    OutlinedButton(
                        onClick = launchGoogleSignIn,
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(text = "Continue with Google")
                    }
                    Spacer(Modifier.height(Dimens.spaceMd))

                    TextButton(
                        onClick = { navigator.push(RegisterScreen()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Don't have an account? Register",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
