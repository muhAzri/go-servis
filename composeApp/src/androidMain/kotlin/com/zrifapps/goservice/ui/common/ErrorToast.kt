package com.zrifapps.goservice.ui.common

import android.content.Context
import com.zrifapps.goservice.core.error.DomainError

/**
 * Surfaces a domain error via the global snackbar host hosted at the nav root.
 * The [Context] receiver is kept for backwards-compatibility with existing call sites.
 */
@Suppress("UnusedReceiverParameter")
fun Context.toastError(error: DomainError) {
    AppSnackbar.showError(error)
}
