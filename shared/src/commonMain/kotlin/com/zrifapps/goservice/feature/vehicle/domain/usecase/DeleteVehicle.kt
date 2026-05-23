package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class DeleteVehicle(
    private val repository: VehicleRepository,
) : UseCase<String, Unit> {
    override suspend fun invoke(params: String): DomainResult<Unit> = repository.softDelete(params)
}
