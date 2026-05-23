package com.zrifapps.goservice.feature.reminder.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.reminder.data.local.ReminderDao
import com.zrifapps.goservice.feature.reminder.data.local.toDomain
import com.zrifapps.goservice.feature.reminder.data.local.toEntity
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderSort
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val dao: ReminderDao,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
) : ReminderRepository {

    override fun observe(filter: ReminderFilter, sort: ReminderSort): Flow<List<Reminder>> =
        dao.observeAll().map { rows ->
            rows.mapNotNull { it.toDomain() }.applyFilter(filter).applySort(sort)
        }

    override fun observeOne(id: String): Flow<Reminder?> =
        dao.observeOne(id).map { it?.toDomain() }

    override fun observeForVehicle(vehicleId: String): Flow<List<Reminder>> =
        dao.observeForVehicle(vehicleId).map { rows -> rows.mapNotNull { it.toDomain() } }

    override suspend fun getById(id: String): DomainResult<Reminder> {
        val entity = dao.getById(id) ?: return DomainResult.Failure(
            DomainError.NotFound(resource = "Reminder", id = id)
        )
        val reminder = entity.toDomain() ?: return DomainResult.Failure(
            DomainError.Storage.ReadFailed("invalid reminder row $id")
        )
        return DomainResult.Success(reminder)
    }

    override suspend fun create(draft: ReminderDraft): DomainResult<Reminder> {
        val now = clock.nowEpochMillis()
        val reminder = Reminder(
            id = idGenerator.newId(),
            vehicleId = draft.vehicleId,
            serviceType = draft.serviceType,
            trackedComponentId = draft.trackedComponentId,
            title = draft.title,
            trigger = draft.trigger,
            note = draft.note,
            notifyDaysBefore = draft.notifyDaysBefore,
            status = ReminderStatus.Active,
            urgency = ReminderUrgency.Ok,
            snoozedUntil = null,
            completedAt = null,
            completedServiceRecordId = null,
            createdAt = now,
            updatedAt = now,
            sync = SyncMetadata.newLocal(now),
        )
        return runStorage {
            dao.upsert(reminder.toEntity())
            reminder
        }
    }

    override suspend fun update(reminder: Reminder): DomainResult<Reminder> {
        val now = clock.nowEpochMillis()
        val updated = reminder.copy(
            updatedAt = now,
            sync = reminder.sync.copy(
                status = SyncStatus.PendingUpdate,
                localUpdatedAt = now,
                version = reminder.sync.version + 1,
            ),
        )
        return runStorage {
            dao.upsert(updated.toEntity())
            updated
        }
    }

    override suspend fun snooze(id: String, until: Long): DomainResult<Reminder> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.snooze(id, until, now, SyncStatus.PendingUpdate.name)
        }.flatMap { getById(id) }
    }

    override suspend fun complete(id: String, serviceRecordId: String?): DomainResult<Reminder> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.complete(id, serviceRecordId, now, SyncStatus.PendingUpdate.name)
        }.flatMap { getById(id) }
    }

    override suspend fun dismiss(id: String): DomainResult<Reminder> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.dismiss(id, now, SyncStatus.PendingUpdate.name)
        }.flatMap { getById(id) }
    }

    override suspend fun softDelete(id: String): DomainResult<Unit> {
        val now = clock.nowEpochMillis()
        return runStorage {
            dao.softDelete(id, now, SyncStatus.PendingDelete.name)
        }
    }

    override suspend fun recomputeUrgency(): DomainResult<Unit> = DomainResult.Success(Unit)

    override suspend fun refresh(): DomainResult<Unit> = DomainResult.Success(Unit)
}

private fun List<Reminder>.applyFilter(filter: ReminderFilter): List<Reminder> {
    var result = this
    if (filter.vehicleIds.isNotEmpty()) result = result.filter { it.vehicleId in filter.vehicleIds }
    if (filter.statuses.isNotEmpty()) result = result.filter { it.status in filter.statuses }
    if (filter.urgencies.isNotEmpty()) result = result.filter { it.urgency in filter.urgencies }
    if (filter.serviceTypes.isNotEmpty()) result = result.filter { it.serviceType in filter.serviceTypes }
    return result
}

private fun List<Reminder>.applySort(sort: ReminderSort): List<Reminder> = when (sort) {
    ReminderSort.UrgencyDesc -> sortedWith(
        compareBy({ urgencyRank(it.urgency) }, { it.trigger.targetDateMillis ?: Long.MAX_VALUE })
    )
    ReminderSort.DueSoonAsc -> sortedBy { it.trigger.targetDateMillis ?: Long.MAX_VALUE }
    ReminderSort.CreatedDesc -> sortedByDescending { it.createdAt }
}

private fun urgencyRank(urgency: ReminderUrgency): Int = when (urgency) {
    ReminderUrgency.Overdue -> 0
    ReminderUrgency.Soon -> 1
    ReminderUrgency.Ok -> 2
}
