package com.morozione.psychologyhelper

import platform.Foundation.NSUserDefaults

actual class AppPreferences {
    actual fun isOnboardingComplete(): Boolean =
        NSUserDefaults.standardUserDefaults.boolForKey("onboarding_done")

    actual fun setOnboardingComplete() {
        NSUserDefaults.standardUserDefaults.setBool(true, "onboarding_done")
    }
}
