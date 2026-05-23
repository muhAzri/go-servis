package com.zrifapps.goservice.feature.service.domain.usecase

import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow

class ObserveServiceHistory(
    private val repository: ServiceRepository,
) {
    data class Params(
        val filter: ServiceFilter = ServiceFilter(),
        val sort: ServiceSort = ServiceSort.DateDesc,
    )

    operator fun invoke(params: Params = Params()): Flow<List<ServiceRecord>> =
        repository.observeRecords(params.filter, params.sort)
}

class ObserveVehicleServiceHistory(
    private val repository: ServiceRepository,
) {
    operator fun invoke(vehicleId: String): Flow<List<ServiceRecord>> =
        repository.observeForVehicle(vehicleId)
}
