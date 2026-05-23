package com.zrifapps.goservice.feature.reminder.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository

class CompleteReminder(
    private val repository: ReminderRepository,
) : UseCase<CompleteReminder.Params, Reminder> {

    data class Params(
        val reminderId: String,
        val serviceRecordId: String? = null,
    )

    override suspend fun invoke(params: Params): DomainResult<Reminder> =
        repository.complete(params.reminderId, params.serviceRecordId)
}

class DismissReminder(
    private val repository: ReminderRepository,
) : UseCase<String, Reminder> {
    override suspend fun invoke(params: String): DomainResult<Reminder> = repository.dismiss(params)
}

class DeleteReminder(
    private val repository: ReminderRepository,
) : UseCase<String, Unit> {
    override suspend fun invoke(params: String): DomainResult<Unit> = repository.softDelete(params)
}
