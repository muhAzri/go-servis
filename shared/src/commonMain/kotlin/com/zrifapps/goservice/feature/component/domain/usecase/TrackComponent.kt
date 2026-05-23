package com.zrifapps.goservice.feature.component.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponentDraft
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository

class TrackComponent(
    private val repository: TrackedComponentRepository,
) : UseCase<TrackedComponentDraft, TrackedComponent> {
    override suspend fun invoke(params: TrackedComponentDraft): DomainResult<TrackedComponent> {
        if (params.vehicleId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("vehicleId"))
        }
        if (params.catalogComponentId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("catalogComponentId"))
        }
        return repository.track(params)
    }
}

class UntrackComponent(
    private val repository: TrackedComponentRepository,
) : UseCase<String, Unit> {
    override suspend fun invoke(params: String): DomainResult<Unit> = repository.untrack(params)
}
