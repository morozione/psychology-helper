package com.morozione.psychologyhelper.domain.util

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

actual class ImagePickerFactory(private val activity: ComponentActivity) {
    actual fun createPicker(): ImagePicker = AndroidImagePicker(activity)
}

class AndroidImagePicker(private val activity: ComponentActivity) : ImagePicker {
    private var onImagePicked: (ByteArray) -> Unit = {}
    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bytes = activity.contentResolver.openInputStream(it)?.readBytes() ?: return@let
            onImagePicked(bytes)
        }
    }

    override fun registerPicker(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }

    override fun pickImage() {
        launcher.launch("image/*")
    }
}
