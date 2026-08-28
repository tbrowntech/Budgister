package com.taylor.budgister

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform