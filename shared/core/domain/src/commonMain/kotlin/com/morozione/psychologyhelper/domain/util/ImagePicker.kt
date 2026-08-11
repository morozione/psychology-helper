package com.morozione.psychologyhelper.domain.util

expect class ImagePickerFactory {
    fun createPicker(): ImagePicker
}

interface ImagePicker {
    fun registerPicker(onImagePicked: (ByteArray) -> Unit)
    fun pickImage()
}
