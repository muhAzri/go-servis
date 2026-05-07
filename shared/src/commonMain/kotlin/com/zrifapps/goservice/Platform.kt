package com.zrifapps.goservice

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform