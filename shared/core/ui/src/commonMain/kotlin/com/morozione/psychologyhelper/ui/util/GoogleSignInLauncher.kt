package com.morozione.psychologyhelper.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleSignInLauncher(onResult: (idToken: String?, error: String?) -> Unit): () -> Unit
