package com.morozione.psychologyhelper.data.di

import com.morozione.psychologyhelper.data.repository.AuthRepositoryImpl
import com.morozione.psychologyhelper.data.repository.JournalRepositoryImpl
import com.morozione.psychologyhelper.data.repository.MoodRepositoryImpl
import com.morozione.psychologyhelper.domain.repository.AuthRepository
import com.morozione.psychologyhelper.domain.repository.JournalRepository
import com.morozione.psychologyhelper.domain.repository.MoodRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val dataModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MoodRepository> { MoodRepositoryImpl(get()) }
    single<JournalRepository> { JournalRepositoryImpl(get()) }
}
