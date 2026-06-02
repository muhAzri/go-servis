package com.zrifapps.goservice.core.notification

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter

class IosOdometerReminderScheduler : OdometerReminderScheduler {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun replaceAll(notifications: List<OdometerScheduledNotification>) {
        // Remove only our previous odometer-scoped identifiers so we don't wipe
        // service reminders scheduled by the other scheduler.
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val ids = (requests ?: emptyList<UNNotificationRequest>())
                .filterIsInstance<UNNotificationRequest>()
                .map { it.identifier }
                .filter { it.startsWith(ID_PREFIX) }
            if (ids.isNotEmpty()) {
                center.removePendingNotificationRequestsWithIdentifiers(ids)
            }
        }
        val tz = TimeZone.currentSystemDefault()
        notifications.forEach { notification ->
            val content = UNMutableNotificationContent().apply {
                setTitle(notification.title)
                setBody(notification.body)
                setCategoryIdentifier(CATEGORY_ID)
                setUserInfo(mapOf<Any?, Any?>(USERINFO_VEHICLE_ID to notification.vehicleId))
            }
            val dateTime = Instant.fromEpochMilliseconds(notification.triggerAtMillis).toLocalDateTime(tz)
            val components = NSDateComponents().apply {
                year = dateTime.year.toLong()
                month = dateTime.month.number.toLong()
                day = dateTime.day.toLong()
                hour = dateTime.hour.toLong()
                minute = dateTime.minute.toLong()
            }
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = components,
                repeats = false,
            )
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = notification.id,
                content = content,
                trigger = trigger,
            )
            center.addNotificationRequest(request, withCompletionHandler = null)
        }
    }

    override suspend fun cancelAll() {
        center.getPendingNotificationRequestsWithCompletionHandler { requests ->
            val ids = (requests ?: emptyList<UNNotificationRequest>())
                .filterIsInstance<UNNotificationRequest>()
                .map { it.identifier }
                .filter { it.startsWith(ID_PREFIX) }
            if (ids.isNotEmpty()) {
                center.removePendingNotificationRequestsWithIdentifiers(ids)
            }
        }
    }

    companion object {
        const val CATEGORY_ID: String = "ODOMETER_REMINDER"
        const val USERINFO_VEHICLE_ID: String = "vehicleId"
        const val ID_PREFIX: String = "odo:"
    }
}
