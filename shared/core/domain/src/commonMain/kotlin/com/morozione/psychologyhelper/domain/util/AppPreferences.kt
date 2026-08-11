package com.morozione.psychologyhelper.domain.util

expect class AppPreferences {
    fun isOnboardingComplete(): Boolean
    fun setOnboardingComplete()
}
