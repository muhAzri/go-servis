package com.zrifapps.goservice.core.notification

/**
 * One concrete local-notification occurrence to fire at [triggerAtMillis].
 * [id] is stable per (reminder, day) so re-planning is idempotent.
 */
data class ScheduledNotification(
    val id: String,
    val reminderId: String,
    val title: String,
    val body: String,
    val triggerAtMillis: Long,
)
