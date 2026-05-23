package com.zrifapps.goservice.feature.service.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository

class DeleteServiceRecord(
    private val repository: ServiceRepository,
) : UseCase<String, Unit> {
    override suspend fun invoke(params: String): DomainResult<Unit> = repository.softDelete(params)
}
