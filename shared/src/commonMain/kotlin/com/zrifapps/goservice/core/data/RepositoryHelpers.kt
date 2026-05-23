package com.zrifapps.goservice.core.data

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult

internal inline fun <T> runStorage(block: () -> T): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (t: Throwable) {
    DomainResult.Failure(DomainError.Storage.WriteFailed(t.message ?: t::class.simpleName ?: "storage error"))
}
