package com.zrifapps.goservice.core.time

fun interface AppClock {
    fun nowEpochMillis(): Long
}

object NoopClock : AppClock {
    override fun nowEpochMillis(): Long = 0L
}
