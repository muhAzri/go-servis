package com.zrifapps.goservice.feature.vehicle.domain.usecase

import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSort
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class ObserveVehicles(
    private val repository: VehicleRepository,
) {
    operator fun invoke(sort: VehicleSort = VehicleSort.InputOrder): Flow<List<Vehicle>> =
        repository.observeVehicles(sort)
}
