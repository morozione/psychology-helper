package com.morozione.psychologyhelper.feature.chat

import org.koin.dsl.module

val chatModule = module {
    factory { ChatScreenModel(get(), get()) }
}
