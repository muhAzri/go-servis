package com.zrifapps.goservice.feature.reminder.domain.usecase

import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ObserveReminder(
    private val repository: ReminderRepository,
) {
    operator fun invoke(reminderId: String): Flow<Reminder?> =
        repository.observeOne(reminderId)
}
