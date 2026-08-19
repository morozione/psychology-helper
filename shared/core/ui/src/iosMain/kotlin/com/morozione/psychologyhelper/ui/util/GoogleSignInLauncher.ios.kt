package com.morozione.psychologyhelper.ui.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleSignInLauncher(onResult: (idToken: String?, error: String?) -> Unit): () -> Unit {
    return { onResult(null, "Google Sign-In isn't available on iOS yet") } // needs GoogleSignIn SDK + URL scheme wiring in Xcode -- not yet implemented
}
