package com.zrifapps.goservice.feature.reminder.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository

class SnoozeReminder(
    private val repository: ReminderRepository,
    private val clock: AppClock,
) : UseCase<SnoozeReminder.Params, Reminder> {

    data class Params(
        val reminderId: String,
        val duration: SnoozeDuration,
    )

    enum class SnoozeDuration(val days: Int) {
        OneDay(1), ThreeDays(3), OneWeek(7), TwoWeeks(14), OneMonth(30);
    }

    override suspend fun invoke(params: Params): DomainResult<Reminder> {
        if (params.reminderId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("reminderId"))
        }
        val now = clock.nowEpochMillis()
        val until = now + params.duration.days.toLong() * MILLIS_PER_DAY
        return repository.snooze(params.reminderId, until)
    }

    private companion object {
        const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
    }
}
