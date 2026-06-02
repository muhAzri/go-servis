package com.zrifapps.goservice.core.notification

import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Expands active/snoozed reminders into daily notification occurrences within the
 * grace window: one per day at [notifyAtHour] from (due - notifyDaysBefore) through
 * the due date. Only reminders with a target DATE produce notifications (km-only
 * reminders surface in-app via odometer urgency instead). Bounded by a horizon and
 * a hard cap so iOS' 64-pending-notification limit is respected.
 */
class ReminderNotificationPlanner(
    private val clock: AppClock,
    private val notifyAtHour: Int = DEFAULT_HOUR,
) {

    fun plan(reminders: List<Reminder>): List<ScheduledNotification> {
        val now = clock.nowEpochMillis()
        val horizonEnd = now + HORIZON_DAYS * MILLIS_PER_DAY
        val tz = TimeZone.currentSystemDefault()
        val out = mutableListOf<ScheduledNotification>()

        for (reminder in reminders) {
            if (reminder.status != ReminderStatus.Active && reminder.status != ReminderStatus.Snoozed) continue
            val due = reminder.trigger.targetDateMillis ?: continue
            val earliest = maxOf(now, reminder.snoozedUntil ?: 0L)
            val leadDays = reminder.notifyDaysBefore.coerceAtLeast(0)
            val windowStart = due - leadDays.toLong() * MILLIS_PER_DAY

            val dueDate = millisToDate(due, tz)
            var day = millisToDate(maxOf(windowStart, earliest), tz)

            while (day <= dueDate) {
                val fireAt = day.atTime(notifyAtHour, 0).toInstant(tz).toEpochMilliseconds()
                if (fireAt in earliest..horizonEnd) {
                    out += ScheduledNotification(
                        id = "${reminder.id}@$day",
                        reminderId = reminder.id,
                        title = reminder.title,
                        body = bodyFor(day.daysUntil(dueDate)),
                        triggerAtMillis = fireAt,
                    )
                }
                day = day.plus(1, DateTimeUnit.DAY)
            }
        }
        return out.sortedBy { it.triggerAtMillis }.take(MAX_NOTIFICATIONS)
    }

    private fun millisToDate(millis: Long, tz: TimeZone): LocalDate =

        Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date

    private fun bodyFor(daysLeft: Int): String = when {
        daysLeft <= 0 -> "Jatuh tempo hari ini. Saatnya servis."
        daysLeft == 1 -> "Tinggal 1 hari lagi menuju jadwal servis."
        else -> "Tinggal $daysLeft hari lagi menuju jadwal servis."
    }

    companion object {
        const val DEFAULT_HOUR: Int = 9
        const val HORIZON_DAYS: Long = 60L
        const val MAX_NOTIFICATIONS: Int = 60
        const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
    }
}
