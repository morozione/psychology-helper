package com.morozione.psychologyhelper.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.morozione.psychologyhelper.domain.util.GOOGLE_WEB_CLIENT_ID
import kotlinx.coroutines.launch

@Composable
actual fun rememberGoogleSignInLauncher(onResult: (idToken: String?, error: String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember {
        GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                    .build()
            )
            .build()
    }

    return {
        scope.launch {
            try {
                val response = credentialManager.getCredential(context, request)
                val credential = response.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    onResult(googleIdTokenCredential.idToken, null)
                } else {
                    onResult(null, "Unexpected credential type")
                }
            } catch (e: GetCredentialException) {
                onResult(null, e.message ?: "Google sign-in was cancelled or failed")
            } catch (e: GoogleIdTokenParsingException) {
                onResult(null, "Failed to parse Google credential")
            }
        }
    }
}
