package com.zrifapps.goservice.feature.component.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponentDraft
import kotlinx.coroutines.flow.Flow

interface TrackedComponentRepository {

    fun observeForVehicle(vehicleId: String): Flow<List<TrackedComponent>>

    fun observeAll(): Flow<List<TrackedComponent>>

    fun observeOne(id: String): Flow<TrackedComponent?>

    suspend fun getById(id: String): DomainResult<TrackedComponent>

    suspend fun track(draft: TrackedComponentDraft): DomainResult<TrackedComponent>

    suspend fun untrack(id: String): DomainResult<Unit>

    suspend fun update(tracked: TrackedComponent): DomainResult<TrackedComponent>

    suspend fun recordService(
        id: String,
        serviceDate: Long,
        serviceOdometer: Distance,
    ): DomainResult<TrackedComponent>

    suspend fun refreshUrgency(vehicleId: String): DomainResult<Unit>

    /** Inserts tracked components whose id is not already present; returns count inserted. */
    suspend fun importMissing(items: List<TrackedComponent>): Int
}
