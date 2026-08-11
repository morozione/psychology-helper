package com.morozione.psychologyhelper

import android.content.Context

actual class AppPreferences(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    actual fun isOnboardingComplete(): Boolean = prefs.getBoolean("onboarding_done", false)

    actual fun setOnboardingComplete() {
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }
}
