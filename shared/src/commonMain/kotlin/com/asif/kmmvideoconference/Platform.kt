package com.asif.kmmvideoconference

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform