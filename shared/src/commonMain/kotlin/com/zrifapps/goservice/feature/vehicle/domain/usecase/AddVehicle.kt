package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleDraft
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class AddVehicle(
    private val repository: VehicleRepository,
) : UseCase<AddVehicle.Params, Vehicle> {

    data class Params(
        val draft: VehicleDraft,
        val ownerProfileId: String,
    )

    override suspend fun invoke(params: Params): DomainResult<Vehicle> {
        val validationError = validate(params.draft)
        if (validationError != null) return DomainResult.Failure(validationError)
        return repository.create(params.draft, params.ownerProfileId)
    }

    private fun validate(draft: VehicleDraft): DomainError? {
        if (draft.nickname.isBlank()) return DomainError.Validation.FieldRequired("nickname")
        if (draft.brand.isBlank()) return DomainError.Validation.FieldRequired("brand")
        val year = draft.year
        if (year != null && (year < MIN_YEAR || year > MAX_YEAR)) {
            return DomainError.Validation.OutOfRange("year", "$MIN_YEAR..$MAX_YEAR")
        }
        return null
    }

    companion object {
        const val MIN_YEAR: Int = 1950
        const val MAX_YEAR: Int = 2100
    }
}
