package com.zrifapps.goservice.core.notification

/**
 * Platform binding for "update KM" notifications. Separate from the service
 * reminder scheduler so the two channels can be enabled/disabled independently
 * and their pending work doesn't trample each other.
 */
interface OdometerReminderScheduler {
    suspend fun replaceAll(notifications: List<OdometerScheduledNotification>)
    suspend fun cancelAll()
}
