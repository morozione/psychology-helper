package com.morozione.psychologyhelper

import androidx.compose.ui.window.ComposeUIViewController
import com.morozione.psychologyhelper.data.di.dataModule
import com.morozione.psychologyhelper.data.di.domainModule
import com.morozione.psychologyhelper.feature.auth.authModule
import com.morozione.psychologyhelper.feature.chat.chatModule
import com.morozione.psychologyhelper.feature.home.homeModule
import com.morozione.psychologyhelper.feature.journal.journalModule
import com.morozione.psychologyhelper.feature.mood.moodModule
import com.morozione.psychologyhelper.feature.profile.profileModule
import org.koin.compose.KoinApplication
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(
            dataModule,
            domainModule,
            authModule,
            homeModule,
            moodModule,
            journalModule,
            profileModule,
            chatModule,
            module { single { AppPreferences() } }
        )
    }) {
        App()
    }
}
