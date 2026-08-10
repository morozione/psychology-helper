package com.morozione.psychologyhelper

import android.app.Application
import com.morozione.psychologyhelper.data.di.dataModule
import com.morozione.psychologyhelper.data.di.domainModule
import com.morozione.psychologyhelper.feature.auth.authModule
import com.morozione.psychologyhelper.feature.chat.chatModule
import com.morozione.psychologyhelper.feature.home.homeModule
import com.morozione.psychologyhelper.feature.journal.journalModule
import com.morozione.psychologyhelper.feature.mood.moodModule
import com.morozione.psychologyhelper.feature.profile.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PsychologyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PsychologyApplication)
            modules(
                dataModule,
                domainModule,
                authModule,
                homeModule,
                moodModule,
                journalModule,
                profileModule,
                chatModule
            )
        }
    }
}
