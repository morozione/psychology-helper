package com.morozione.psychologyhelper.feature.mood

import org.koin.dsl.module

val moodModule = module {
    factory { MoodScreenModel(get(), get()) }
}
