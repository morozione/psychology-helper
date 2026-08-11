package com.morozione.psychologyhelper.ui.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePickerLauncher(onImagePicked: (ByteArray) -> Unit): () -> Unit {
    return {} // iOS image picker requires UIKit integration -- not yet implemented
}
