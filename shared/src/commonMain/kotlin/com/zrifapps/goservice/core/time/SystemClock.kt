package com.zrifapps.goservice.core.time

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SystemClock : AppClock {
    @OptIn(ExperimentalTime::class)
    override fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
