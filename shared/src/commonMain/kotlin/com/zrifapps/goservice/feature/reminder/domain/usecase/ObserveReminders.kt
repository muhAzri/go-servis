package com.zrifapps.goservice.feature.reminder.domain.usecase

import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderSort
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ObserveReminders(
    private val repository: ReminderRepository,
) {
    data class Params(
        val filter: ReminderFilter = ReminderFilter(),
        val sort: ReminderSort = ReminderSort.UrgencyDesc,
    )

    operator fun invoke(params: Params = Params()): Flow<List<Reminder>> =
        repository.observe(params.filter, params.sort)
}
