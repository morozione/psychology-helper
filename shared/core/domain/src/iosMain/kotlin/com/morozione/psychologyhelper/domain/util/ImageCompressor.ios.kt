package com.morozione.psychologyhelper.domain.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (result.isNotEmpty()) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return result
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}

// Re-encodes at a lower JPEG quality without resizing -- still meaningfully smaller than a raw
// camera photo. Resizing on iOS needs UIGraphicsImageRenderer/CGSize interop that isn't worth the
// added risk here; quality-only compression uses the same NSData/UIImage APIs already verified
// working in the image picker.
@OptIn(ExperimentalForeignApi::class)
actual fun compressImage(bytes: ByteArray, maxDimensionPx: Int, quality: Float): ByteArray {
    val image = UIImage(data = bytes.toNSData()) ?: return bytes
    val compressed = UIImageJPEGRepresentation(image, quality.toDouble())
    return compressed?.toByteArray() ?: bytes
}
