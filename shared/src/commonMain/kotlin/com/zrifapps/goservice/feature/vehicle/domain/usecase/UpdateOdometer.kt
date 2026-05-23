package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class UpdateOdometer(
    private val repository: VehicleRepository,
) : UseCase<UpdateOdometer.Params, Vehicle> {

    data class Params(
        val vehicleId: String,
        val odometer: Distance,
        val allowRollback: Boolean = false,
    )

    override suspend fun invoke(params: Params): DomainResult<Vehicle> {
        if (!params.allowRollback) {
            val current = repository.getById(params.vehicleId)
            val currentVehicle = current.getOrNull() ?: return current
            if (params.odometer < currentVehicle.odometer) {
                return DomainResult.Failure(
                    DomainError.Validation.OutOfRange(
                        field = "odometer",
                        reason = "tidak boleh lebih kecil dari nilai saat ini",
                    )
                )
            }
        }
        return repository.updateOdometer(params.vehicleId, params.odometer)
    }
}
