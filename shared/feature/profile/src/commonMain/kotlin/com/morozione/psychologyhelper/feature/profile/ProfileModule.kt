package com.morozione.psychologyhelper.feature.profile

import org.koin.dsl.module

val profileModule = module {
    factory { ProfileScreenModel(get(), get(), get(), get(), get(), get()) }
}
