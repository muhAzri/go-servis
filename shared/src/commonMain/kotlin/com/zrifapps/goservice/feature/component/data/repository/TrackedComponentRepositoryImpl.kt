package com.zrifapps.goservice.feature.component.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.data.local.TrackedComponentDao
import com.zrifapps.goservice.feature.component.data.local.toDomain
import com.zrifapps.goservice.feature.component.data.local.toEntity
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponentDraft
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackedComponentRepositoryImpl(
    private val dao: TrackedComponentDao,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
) : TrackedComponentRepository {

    override fun observeForVehicle(vehicleId: String): Flow<List<TrackedComponent>> =
        dao.observeForVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeAll(): Flow<List<TrackedComponent>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeOne(id: String): Flow<TrackedComponent?> =
        dao.observeOne(id).map { it?.toDomain() }

    override suspend fun getById(id: String): DomainResult<TrackedComponent> {
        val entity = dao.getById(id) ?: return DomainResult.Failure(
            DomainError.NotFound(resource = "TrackedComponent", id = id)
        )
        return DomainResult.Success(entity.toDomain())
    }

    override suspend fun track(draft: TrackedComponentDraft): DomainResult<TrackedComponent> {
        val now = clock.nowEpochMillis()
        val tracked = TrackedComponent(
            id = idGenerator.newId(),
            vehicleId = draft.vehicleId,
            catalogComponentId = draft.catalogComponentId,
            customName = draft.customName,
            lastServiceDate = draft.lastServiceDate,
            lastServiceOdometer = draft.lastServiceOdometer,
            nextServiceDate = null,
            nextServiceOdometer = null,
            intervalKmOverride = draft.intervalKmOverride,
            intervalDaysOverride = draft.intervalDaysOverride,
            urgency = ComponentUrgency.Ok,
            createdAt = now,
            updatedAt = now,
            sync = SyncMetadata.newLocal(now),
        )
        return runStorage {
            dao.upsert(tracked.toEntity())
            tracked
        }
    }

    override suspend fun untrack(id: String): DomainResult<Unit> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.softDelete(id, now, SyncStatus.PendingDelete.name)
        }
    }

    override suspend fun update(tracked: TrackedComponent): DomainResult<TrackedComponent> {
        val now = clock.nowEpochMillis()
        val updated = tracked.copy(
            updatedAt = now,
            sync = tracked.sync.copy(
                status = SyncStatus.PendingUpdate,
                localUpdatedAt = now,
                version = tracked.sync.version + 1,
            ),
        )
        return runStorage {
            dao.upsert(updated.toEntity())
            updated
        }
    }

    override suspend fun recordService(
        id: String,
        serviceDate: Long,
        serviceOdometer: Distance,
    ): DomainResult<TrackedComponent> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.recordService(
                id = id,
                date = serviceDate,
                km = serviceOdometer.kilometers,
                now = now,
                syncStatus = SyncStatus.PendingUpdate.name,
            )
        }.flatMap { getById(id) }
    }

    override suspend fun refreshUrgency(vehicleId: String): DomainResult<Unit> =
        DomainResult.Success(Unit)

    override suspend fun importMissing(items: List<TrackedComponent>): Int {
        var inserted = 0
        for (component in items) {
            if (dao.getById(component.id) == null) {
                dao.upsert(component.toEntity())
                inserted++
            }
        }
        return inserted
    }
}
