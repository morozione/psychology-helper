package com.morozione.psychologyhelper.ui.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toJpegByteArray(quality: Float): ByteArray {
    val output = ByteArrayOutputStream()
    asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, (quality * 100).toInt().coerceIn(0, 100), output)
    return output.toByteArray()
}
