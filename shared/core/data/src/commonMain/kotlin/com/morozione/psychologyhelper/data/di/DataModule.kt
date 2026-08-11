package com.morozione.psychologyhelper.data.di

import com.morozione.psychologyhelper.data.getGeminiApiKey
import com.morozione.psychologyhelper.data.remote.GeminiService
import com.morozione.psychologyhelper.data.repository.AuthRepositoryImpl
import com.morozione.psychologyhelper.data.repository.ChatRepositoryImpl
import com.morozione.psychologyhelper.data.repository.JournalRepositoryImpl
import com.morozione.psychologyhelper.data.repository.MoodRepositoryImpl
import com.morozione.psychologyhelper.data.repository.UserRepositoryImpl
import com.morozione.psychologyhelper.domain.repository.AuthRepository
import com.morozione.psychologyhelper.domain.repository.ChatRepository
import com.morozione.psychologyhelper.domain.repository.JournalRepository
import com.morozione.psychologyhelper.domain.repository.MoodRepository
import com.morozione.psychologyhelper.domain.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<MoodRepository> { MoodRepositoryImpl(get()) }
    single<JournalRepository> { JournalRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(Logging) {
                level = LogLevel.NONE
            }
        }
    }
    single { GeminiService(get(), apiKey = getGeminiApiKey()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }
}
