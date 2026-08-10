package com.morozione.psychologyhelper.domain.entity

enum class Mood(val emoji: String, val label: String) {
    GREAT("😄", "Great"),
    GOOD("🙂", "Good"),
    OKAY("😐", "Okay"),
    BAD("😔", "Bad"),
    TERRIBLE("😢", "Terrible")
}
