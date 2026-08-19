package com.morozione.psychologyhelper.data.di

import com.morozione.psychologyhelper.domain.usecase.auth.GetCurrentUserUseCase
import com.morozione.psychologyhelper.domain.usecase.auth.LoginUseCase
import com.morozione.psychologyhelper.domain.usecase.auth.LoginWithGoogleUseCase
import com.morozione.psychologyhelper.domain.usecase.auth.LogoutUseCase
import com.morozione.psychologyhelper.domain.usecase.auth.RegisterUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.AddJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.DeleteJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.GetJournalEntriesUseCase
import com.morozione.psychologyhelper.domain.usecase.journal.UpdateJournalEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.AddMoodEntryUseCase
import com.morozione.psychologyhelper.domain.usecase.mood.GetMoodEntriesUseCase
import com.morozione.psychologyhelper.domain.usecase.user.GetUserProfileUseCase
import com.morozione.psychologyhelper.domain.usecase.user.UpdateProfilePhotoUrlUseCase
import com.morozione.psychologyhelper.domain.usecase.user.UploadProfilePhotoUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { LoginWithGoogleUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GetMoodEntriesUseCase(get()) }
    factory { AddMoodEntryUseCase(get()) }
    factory { GetJournalEntriesUseCase(get()) }
    factory { AddJournalEntryUseCase(get()) }
    factory { UpdateJournalEntryUseCase(get()) }
    factory { DeleteJournalEntryUseCase(get()) }
    factory { UploadProfilePhotoUseCase(get()) }
    factory { UpdateProfilePhotoUrlUseCase(get()) }
    factory { GetUserProfileUseCase(get()) }
}
