package com.zrifapps.goservice.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Bridges a tapped "update KM" notification into in-app navigation. */
object OdometerDeepLinks {

    private val _pending = MutableStateFlow<String?>(null)
    val pending: StateFlow<String?> = _pending.asStateFlow()

    fun open(vehicleId: String) {
        _pending.value = vehicleId
    }

    fun consume() {
        _pending.value = null
    }
}
