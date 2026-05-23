package com.zrifapps.goservice.core.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import kotlinx.coroutines.flow.Flow

interface UseCase<in P, out R> {
    suspend operator fun invoke(params: P): DomainResult<R>
}

interface FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<DomainResult<R>>
}

interface NoArgUseCase<out R> {
    suspend operator fun invoke(): DomainResult<R>
}

interface NoArgFlowUseCase<out R> {
    operator fun invoke(): Flow<DomainResult<R>>
}

fun <R> domainFailure(error: DomainError): DomainResult<R> = DomainResult.Failure(error)
fun <R> domainSuccess(value: R): DomainResult<R> = DomainResult.Success(value)
