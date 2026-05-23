package com.zrifapps.goservice.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun interface Cancellable {
    fun cancel()
}

internal fun Job.asCancellable(): Cancellable = Cancellable { cancel() }

fun <T> Flow<T>.subscribeOn(
    scope: CoroutineScope,
    onEach: (T) -> Unit,
): Cancellable = scope.launch { collect { onEach(it) } }.asCancellable()
