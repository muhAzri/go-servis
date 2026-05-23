package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class GetVehicle(
    private val repository: VehicleRepository,
) : UseCase<String, Vehicle> {
    override suspend fun invoke(params: String): DomainResult<Vehicle> = repository.getById(params)
}
