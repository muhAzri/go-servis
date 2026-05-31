package com.zrifapps.goservice.core.notification

import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

/**
 * One-shot reschedule: re-plan all active/snoozed reminders and hand them to the
 * platform scheduler, honoring the "service reminders" settings toggle. Used by
 * background entry points (e.g. notification snooze action) where the reactive
 * collector in app startup may not be alive.
 */
class RescheduleReminderNotifications(
    private val reminderRepository: ReminderRepository,
    private val settingsRepository: SettingsRepository,
    private val planner: ReminderNotificationPlanner,
    private val scheduler: ReminderNotificationScheduler,
) {
    suspend operator fun invoke() {
        val enabled = settingsRepository.observe().first().serviceReminderNotificationsEnabled
        if (!enabled) {
            scheduler.cancelAll()
            return
        }
        val reminders = reminderRepository.observe(
            ReminderFilter(statuses = setOf(ReminderStatus.Active, ReminderStatus.Snoozed)),
        ).first()
        scheduler.replaceAll(planner.plan(reminders))
    }
}
