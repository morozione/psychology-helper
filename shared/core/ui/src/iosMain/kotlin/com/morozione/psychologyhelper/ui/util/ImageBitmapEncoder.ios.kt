package com.morozione.psychologyhelper.ui.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toJpegByteArray(quality: Float): ByteArray {
    val skiaImage = Image.makeFromBitmap(asSkiaBitmap())
    val data = skiaImage.encodeToData(
        format = EncodedImageFormat.JPEG,
        quality = (quality * 100).toInt().coerceIn(0, 100)
    )
    return data?.bytes ?: ByteArray(0)
}
