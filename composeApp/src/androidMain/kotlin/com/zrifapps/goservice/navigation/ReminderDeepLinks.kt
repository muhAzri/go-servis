package com.zrifapps.goservice.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Bridges a tapped reminder notification into in-app navigation. */
object ReminderDeepLinks {

    private val _pending = MutableStateFlow<String?>(null)
    val pending: StateFlow<String?> = _pending.asStateFlow()

    fun open(reminderId: String) {
        _pending.value = reminderId
    }

    fun consume() {
        _pending.value = null
    }
}
