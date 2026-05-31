package com.zrifapps.goservice.feature.vehicle.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleDao
import com.zrifapps.goservice.feature.vehicle.data.local.toDomain
import com.zrifapps.goservice.feature.vehicle.data.local.toEntity
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleDraft
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSort
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleSummary
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val dao: VehicleDao,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
) : VehicleRepository {

    override fun observeVehicles(sort: VehicleSort): Flow<List<Vehicle>> {
        val source = when (sort) {
            VehicleSort.Alphabetical -> dao.observeAllAlphabetical()
            VehicleSort.OdometerAsc -> dao.observeAllOdometerAsc()
            VehicleSort.OdometerDesc -> dao.observeAllOdometerDesc()
            VehicleSort.InputOrder, VehicleSort.ReminderUrgency -> dao.observeAll()
        }
        return source.map { list -> list.map { it.toDomain() } }
    }

    override fun observeVehicle(id: String): Flow<Vehicle?> =
        dao.observeOne(id).map { it?.toDomain() }

    override fun observeVehicleSummary(id: String): Flow<VehicleSummary?> = flowOf(null)

    override suspend fun getById(id: String): DomainResult<Vehicle> {
        val entity = dao.getById(id) ?: return DomainResult.Failure(
            DomainError.NotFound(resource = "Vehicle", id = id)
        )
        return DomainResult.Success(entity.toDomain())
    }

    override suspend fun create(draft: VehicleDraft, ownerProfileId: String): DomainResult<Vehicle> {
        val now = clock.nowEpochMillis()
        val vehicle = Vehicle(
            id = idGenerator.newId(),
            ownerProfileId = ownerProfileId,
            nickname = draft.nickname,
            type = draft.type,
            subtypeId = draft.subtypeId,
            brand = draft.brand,
            model = draft.model,
            year = draft.year,
            plateNumber = draft.plateNumber,
            odometer = draft.odometer,
            color = draft.color,
            createdAt = now,
            updatedAt = now,
            sync = SyncMetadata.newLocal(now),
        )
        return runStorage {
            dao.upsert(vehicle.toEntity())
            vehicle
        }
    }

    override suspend fun update(vehicle: Vehicle): DomainResult<Vehicle> {
        val now = clock.nowEpochMillis()
        val updated = vehicle.copy(
            updatedAt = now,
            sync = vehicle.sync.copy(
                status = SyncStatus.PendingUpdate,
                localUpdatedAt = now,
                version = vehicle.sync.version + 1,
            ),
        )
        return runStorage {
            dao.upsert(updated.toEntity())
            updated
        }
    }

    override suspend fun updateOdometer(id: String, odometer: Distance): DomainResult<Vehicle> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.updateOdometer(id, odometer.kilometers, now, SyncStatus.PendingUpdate.name)
        }.flatMap {
            getById(id)
        }
    }

    override suspend fun softDelete(id: String): DomainResult<Unit> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.softDelete(id, now, SyncStatus.PendingDelete.name)
        }
    }

    override suspend fun refresh(): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun importMissing(items: List<Vehicle>): Int {
        var inserted = 0
        for (vehicle in items) {
            if (dao.getById(vehicle.id) == null) {
                dao.upsert(vehicle.toEntity())
                inserted++
            }
        }
        return inserted
    }
}
