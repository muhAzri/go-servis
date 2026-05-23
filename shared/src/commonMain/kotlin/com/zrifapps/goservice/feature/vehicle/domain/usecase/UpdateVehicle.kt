package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class UpdateVehicle(
    private val repository: VehicleRepository,
) : UseCase<Vehicle, Vehicle> {
    override suspend fun invoke(params: Vehicle): DomainResult<Vehicle> {
        if (params.nickname.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("nickname"))
        }
        if (params.brand.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("brand"))
        }
        return repository.update(params)
    }
}
