package com.zrifapps.goservice.core.notification

/**
 * Platform binding that turns planned [ScheduledNotification]s into real OS local
 * notifications (WorkManager on Android, UNUserNotificationCenter on iOS).
 * Implementations are provided per platform via the platform Koin module.
 */
interface ReminderNotificationScheduler {

    /** Cancel everything previously scheduled, then schedule [notifications]. */
    suspend fun replaceAll(notifications: List<ScheduledNotification>)

    /** Cancel all pending reminder notifications. */
    suspend fun cancelAll()
}
