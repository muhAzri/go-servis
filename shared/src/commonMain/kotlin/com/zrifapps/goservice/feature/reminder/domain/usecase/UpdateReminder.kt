package com.zrifapps.goservice.feature.reminder.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository

class UpdateReminder(
    private val repository: ReminderRepository,
) : UseCase<Reminder, Reminder> {

    override suspend fun invoke(params: Reminder): DomainResult<Reminder> {
        if (params.vehicleId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("vehicleId"))
        }
        if (params.title.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("title"))
        }
        if (params.notifyDaysBefore < 0) {
            return DomainResult.Failure(
                DomainError.Validation.OutOfRange("notifyDaysBefore", ">= 0"),
            )
        }
        return repository.update(params)
    }
}
