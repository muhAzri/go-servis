package com.zrifapps.goservice.core.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

/**
 * Realizes planned notifications as WorkManager one-time jobs (survive process
 * death and reboot). Each occurrence is unique-named by its stable id, and all are
 * tagged so [cancelAll]/[replaceAll] can clear the previous schedule wholesale.
 */
class AndroidReminderNotificationScheduler(
    private val context: Context,
) : ReminderNotificationScheduler {

    private val workManager: WorkManager get() = WorkManager.getInstance(context)

    override suspend fun replaceAll(notifications: List<ScheduledNotification>) {
        workManager.cancelAllWorkByTag(TAG)
        val now = System.currentTimeMillis()
        notifications.forEach { notification ->
            val delay = (notification.triggerAtMillis - now).coerceAtLeast(0L)
            val request = OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        ReminderNotificationWorker.KEY_REMINDER_ID to notification.reminderId,
                        ReminderNotificationWorker.KEY_TITLE to notification.title,
                        ReminderNotificationWorker.KEY_BODY to notification.body,
                    ),
                )
                .addTag(TAG)
                .build()
            workManager.enqueueUniqueWork(notification.id, ExistingWorkPolicy.REPLACE, request)
        }
    }

    override suspend fun cancelAll() {
        workManager.cancelAllWorkByTag(TAG)
    }

    private companion object {
        const val TAG = "reminder_notification"
    }
}
