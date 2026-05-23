package com.zrifapps.goservice.feature.service.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecordDraft
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class RecordService(
    private val serviceRepository: ServiceRepository,
    private val vehicleRepository: VehicleRepository,
) : UseCase<ServiceRecordDraft, ServiceRecord> {

    override suspend fun invoke(params: ServiceRecordDraft): DomainResult<ServiceRecord> {
        if (params.vehicleId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("vehicleId"))
        }
        if (params.serviceDate <= 0L) {
            return DomainResult.Failure(DomainError.Validation.InvalidFormat("serviceDate", "harus > 0"))
        }
        val created = serviceRepository.create(params)
        if (created is DomainResult.Success) {
            syncVehicleOdometer(params.vehicleId, params.odometer)
        }
        return created
    }

    private suspend fun syncVehicleOdometer(vehicleId: String, odometer: Distance) {
        val current = vehicleRepository.getById(vehicleId).getOrNull() ?: return
        if (odometer > current.odometer) {
            vehicleRepository.updateOdometer(vehicleId, odometer)
        }
    }
}
