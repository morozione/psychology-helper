package com.morozione.psychologyhelper.domain.util

actual class ImagePickerFactory {
    actual fun createPicker(): ImagePicker = IosImagePicker()
}

class IosImagePicker : ImagePicker {
    override fun registerPicker(onImagePicked: (ByteArray) -> Unit) {}
    override fun pickImage() {} // iOS implementation requires UIKit — stub for now
}
