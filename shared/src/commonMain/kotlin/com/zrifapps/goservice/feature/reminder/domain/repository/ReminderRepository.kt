package com.zrifapps.goservice.feature.reminder.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderSort
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun observe(
        filter: ReminderFilter = ReminderFilter(),
        sort: ReminderSort = ReminderSort.UrgencyDesc,
    ): Flow<List<Reminder>>

    fun observeOne(id: String): Flow<Reminder?>

    fun observeForVehicle(vehicleId: String): Flow<List<Reminder>>

    suspend fun getById(id: String): DomainResult<Reminder>

    suspend fun create(draft: ReminderDraft): DomainResult<Reminder>

    suspend fun update(reminder: Reminder): DomainResult<Reminder>

    suspend fun snooze(id: String, until: Long): DomainResult<Reminder>

    suspend fun complete(id: String, serviceRecordId: String?): DomainResult<Reminder>

    suspend fun dismiss(id: String): DomainResult<Reminder>

    suspend fun softDelete(id: String): DomainResult<Unit>

    suspend fun recomputeUrgency(): DomainResult<Unit>

    suspend fun refresh(): DomainResult<Unit>
}
