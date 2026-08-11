package com.morozione.psychologyhelper.data.util

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.toFirebaseData(): Data = usePinned { pinned ->
    Data(NSData.create(bytes = pinned.addressOf(0), length = size.toULong()))
}
