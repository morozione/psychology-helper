package com.morozione.psychologyhelper.domain.entity

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null
)
