package com.morozione.psychologyhelper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform