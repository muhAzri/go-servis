package com.zrifapps.goservice.feature.service.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.paging.Page
import com.zrifapps.goservice.core.paging.PageRequest
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.service.data.local.ServiceRecordDao
import com.zrifapps.goservice.feature.service.data.local.toDomain
import com.zrifapps.goservice.feature.service.data.local.toEntity
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecordDraft
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServiceRepositoryImpl(
    private val dao: ServiceRecordDao,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
) : ServiceRepository {

    override fun observeRecords(filter: ServiceFilter, sort: ServiceSort): Flow<List<ServiceRecord>> =
        dao.observeAll().map { rows ->
            rows.map { it.toDomain() }
                .applyFilter(filter)
                .applySort(sort)
        }

    override fun observeOne(id: String): Flow<ServiceRecord?> =
        dao.observeOne(id).map { it?.toDomain() }

    override fun observeForVehicle(vehicleId: String): Flow<List<ServiceRecord>> =
        dao.observeForVehicle(vehicleId).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: String): DomainResult<ServiceRecord> {
        val entity = dao.getById(id) ?: return DomainResult.Failure(
            DomainError.NotFound(resource = "ServiceRecord", id = id)
        )
        return DomainResult.Success(entity.toDomain())
    }

    override suspend fun query(
        filter: ServiceFilter,
        sort: ServiceSort,
        page: PageRequest,
    ): DomainResult<Page<ServiceRecord>> {
        return DomainResult.Failure(
            DomainError.Storage.ReadFailed("paged query not implemented yet; observe instead")
        )
    }

    override suspend fun create(draft: ServiceRecordDraft): DomainResult<ServiceRecord> {
        val now = clock.nowEpochMillis()
        val record = ServiceRecord(
            id = idGenerator.newId(),
            vehicleId = draft.vehicleId,
            serviceType = draft.serviceType,
            serviceDate = draft.serviceDate,
            odometer = draft.odometer,
            workshop = draft.workshop,
            cost = draft.cost,
            note = draft.note,
            componentIds = draft.componentIds,
            sourceReminderId = draft.sourceReminderId,
            createdAt = now,
            updatedAt = now,
            sync = SyncMetadata.newLocal(now),
        )
        return runStorage {
            dao.upsertWithComponents(record.toEntity(), record.componentIds)
            record
        }
    }

    override suspend fun update(record: ServiceRecord): DomainResult<ServiceRecord> {
        val now = clock.nowEpochMillis()
        val updated = record.copy(
            updatedAt = now,
            sync = record.sync.copy(
                status = SyncStatus.PendingUpdate,
                localUpdatedAt = now,
                version = record.sync.version + 1,
            ),
        )
        return runStorage {
            dao.upsertWithComponents(updated.toEntity(), updated.componentIds)
            updated
        }
    }

    override suspend fun softDelete(id: String): DomainResult<Unit> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.softDelete(id, now, SyncStatus.PendingDelete.name)
        }
    }

    override suspend fun refresh(): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun importMissing(items: List<ServiceRecord>): Int {
        var inserted = 0
        for (record in items) {
            if (dao.getById(record.id) == null) {
                dao.upsertWithComponents(record.toEntity(), record.componentIds)
                inserted++
            }
        }
        return inserted
    }
}

private fun List<ServiceRecord>.applyFilter(filter: ServiceFilter): List<ServiceRecord> {
    var result = this
    if (filter.vehicleIds.isNotEmpty()) result = result.filter { it.vehicleId in filter.vehicleIds }
    if (filter.serviceTypes.isNotEmpty()) result = result.filter { it.serviceType in filter.serviceTypes }
    if (filter.componentIds.isNotEmpty()) {
        result = result.filter { record -> record.componentIds.any { it in filter.componentIds } }
    }
    filter.dateRange?.let { range -> result = result.filter { it.serviceDate in range } }
    filter.minCost?.let { min -> result = result.filter { it.cost >= min } }
    filter.maxCost?.let { max -> result = result.filter { it.cost <= max } }
    filter.query?.takeIf { it.isNotBlank() }?.let { q ->
        result = result.filter {
            it.workshop?.contains(q, ignoreCase = true) == true ||
                it.note?.contains(q, ignoreCase = true) == true
        }
    }
    return result
}

private fun List<ServiceRecord>.applySort(sort: ServiceSort): List<ServiceRecord> = when (sort) {
    ServiceSort.DateDesc -> sortedByDescending { it.serviceDate }
    ServiceSort.DateAsc -> sortedBy { it.serviceDate }
    ServiceSort.OdometerDesc -> sortedByDescending { it.odometer.kilometers }
    ServiceSort.OdometerAsc -> sortedBy { it.odometer.kilometers }
    ServiceSort.CostDesc -> sortedByDescending { it.cost.amountIdr }
    ServiceSort.CostAsc -> sortedBy { it.cost.amountIdr }
}
