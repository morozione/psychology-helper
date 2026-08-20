package com.morozione.psychologyhelper.domain.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual fun compressImage(bytes: ByteArray, maxDimensionPx: Int, quality: Float): ByteArray {
    val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
    val largestSide = maxOf(original.width, original.height)
    val scale = if (largestSide > maxDimensionPx) maxDimensionPx.toFloat() / largestSide else 1f
    val scaled = if (scale < 1f) {
        Bitmap.createScaledBitmap(
            original,
            (original.width * scale).toInt().coerceAtLeast(1),
            (original.height * scale).toInt().coerceAtLeast(1),
            true
        )
    } else {
        original
    }
    val output = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, (quality * 100).toInt().coerceIn(0, 100), output)
    if (scaled !== original) scaled.recycle()
    original.recycle()
    return output.toByteArray()
}
