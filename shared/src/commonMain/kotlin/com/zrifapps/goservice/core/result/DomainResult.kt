package com.zrifapps.goservice.core.result

import com.zrifapps.goservice.core.error.DomainError

sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Failure(val error: DomainError) : DomainResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data
    fun errorOrNull(): DomainError? = (this as? Failure)?.error

    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    inline fun <R> flatMap(transform: (T) -> DomainResult<R>): DomainResult<R> = when (this) {
        is Success -> transform(data)
        is Failure -> this
    }

    inline fun onSuccess(action: (T) -> Unit): DomainResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onFailure(action: (DomainError) -> Unit): DomainResult<T> {
        if (this is Failure) action(error)
        return this
    }

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (DomainError) -> R,
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(error)
    }

    companion object {
        fun <T> success(value: T): DomainResult<T> = Success(value)
        fun failure(error: DomainError): DomainResult<Nothing> = Failure(error)
    }
}

inline fun <T> domainResultOf(block: () -> T): DomainResult<T> = try {
    DomainResult.Success(block())
} catch (t: Throwable) {
    DomainResult.Failure(DomainError.Unknown(cause = t, message = t.message ?: "unknown"))
}
