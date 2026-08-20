package com.morozione.psychologyhelper.ui.util

import androidx.compose.ui.graphics.ImageBitmap

/** Encodes a captured/composed [ImageBitmap] (e.g. from a crop screenshot) as JPEG bytes. */
expect fun ImageBitmap.toJpegByteArray(quality: Float = 0.85f): ByteArray
