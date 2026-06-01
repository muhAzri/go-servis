package com.zrifapps.goservice.feature.service.domain.usecase

import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow

class ObserveVehicleServiceHistory(
    private val repository: ServiceRepository,
) {
    operator fun invoke(vehicleId: String): Flow<List<ServiceRecord>> =
        repository.observeForVehicle(vehicleId)
}
