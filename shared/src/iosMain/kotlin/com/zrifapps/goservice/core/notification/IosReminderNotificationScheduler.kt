package com.zrifapps.goservice.core.notification

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

/**
 * Schedules each planned occurrence as a UNCalendarNotificationTrigger. The
 * planner caps the count so we stay under iOS' 64 pending-notification limit, and
 * the whole set is rebuilt on every change (reactive collector / app launch).
 */
class IosReminderNotificationScheduler : ReminderNotificationScheduler {

    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun replaceAll(notifications: List<ScheduledNotification>) {
        center.removeAllPendingNotificationRequests()
        val tz = TimeZone.currentSystemDefault()
        notifications.forEach { notification ->
            val content = UNMutableNotificationContent().apply {
                setTitle(notification.title)
                setBody(notification.body)
                setSound(UNNotificationSound.defaultSound())
                setCategoryIdentifier(CATEGORY_ID)
                setUserInfo(mapOf<Any?, Any?>(USERINFO_REMINDER_ID to notification.reminderId))
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
        center.removeAllPendingNotificationRequests()
    }

    companion object {
        const val CATEGORY_ID: String = "SERVICE_REMINDER"
        const val USERINFO_REMINDER_ID: String = "reminderId"
    }
}
