package com.zrifapps.goservice.ui.common

import com.zrifapps.goservice.core.error.DomainError
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Process-wide sink for transient user-facing messages (errors, info).
 * A single SnackbarHost mounted near the nav root observes this flow and renders
 * messages on top of whatever screen is currently visible.
 */
object AppSnackbar {
    private val _messages = MutableSharedFlow<String>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun show(message: String) {
        _messages.tryEmit(message)
    }

    fun showError(error: DomainError) {
        show("Gagal: ${error.message}")
    }
}
