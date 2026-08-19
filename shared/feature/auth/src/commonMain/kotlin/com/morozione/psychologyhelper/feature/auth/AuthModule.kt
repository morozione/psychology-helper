package com.morozione.psychologyhelper.feature.auth

import org.koin.dsl.module

val authModule = module {
    factory { LoginScreenModel(get(), get()) }
    factory { RegisterScreenModel(get()) }
}
