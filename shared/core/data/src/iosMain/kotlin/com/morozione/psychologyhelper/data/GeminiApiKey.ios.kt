package com.morozione.psychologyhelper.data

import platform.Foundation.NSBundle

actual fun getGeminiApiKey(): String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("GeminiApiKey") as? String ?: ""
