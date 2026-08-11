package com.morozione.psychologyhelper.feature.journal

import org.koin.dsl.module

val journalModule = module {
    factory { JournalScreenModel(get(), get()) }
    factory { JournalEntryScreenModel(get(), get()) }
}
