package com.morozione.psychologyhelper.domain.util

/**
 * Resizes [bytes] so neither dimension exceeds [maxDimensionPx] and re-encodes as JPEG at
 * [quality] (0f-1f). Keeps profile photo uploads to a few hundred KB instead of a raw camera
 * photo's several MB.
 */
expect fun compressImage(bytes: ByteArray, maxDimensionPx: Int = 1024, quality: Float = 0.75f): ByteArray
