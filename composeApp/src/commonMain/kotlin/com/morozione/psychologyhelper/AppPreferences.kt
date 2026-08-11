package com.morozione.psychologyhelper

expect class AppPreferences {
    fun isOnboardingComplete(): Boolean
    fun setOnboardingComplete()
}
