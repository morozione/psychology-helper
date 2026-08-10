package com.morozione.psychologyhelper.feature.home

import org.koin.dsl.module

val homeModule = module {
    factory { HomeScreenModel(get(), get()) }
}
