package com.zrifapps.goservice.feature.vehicle.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleDraft
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSort
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSummary
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {

    fun observeVehicles(sort: VehicleSort = VehicleSort.InputOrder): Flow<List<Vehicle>>

    fun observeVehicle(id: String): Flow<Vehicle?>

    fun observeVehicleSummary(id: String): Flow<VehicleSummary?>

    suspend fun getById(id: String): DomainResult<Vehicle>

    suspend fun create(draft: VehicleDraft, ownerProfileId: String): DomainResult<Vehicle>

    suspend fun update(vehicle: Vehicle): DomainResult<Vehicle>

    suspend fun updateOdometer(id: String, odometer: Distance): DomainResult<Vehicle>

    suspend fun softDelete(id: String): DomainResult<Unit>

    suspend fun refresh(): DomainResult<Unit>
}

interface VehicleSubtypeRepository {
    suspend fun forType(type: com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType):
        DomainResult<List<com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSubtype>>

    suspend fun labelOf(
        type: com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType,
        subtypeId: String,
    ): DomainResult<String>

    suspend fun defaultIdFor(
        type: com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType,
    ): DomainResult<String>
}
