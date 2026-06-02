package com.zrifapps.goservice.core.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class AndroidOdometerReminderScheduler(
    private val context: Context,
) : OdometerReminderScheduler {

    private val workManager: WorkManager get() = WorkManager.getInstance(context)

    override suspend fun replaceAll(notifications: List<OdometerScheduledNotification>) {
        workManager.cancelAllWorkByTag(TAG)
        val now = System.currentTimeMillis()
        notifications.forEach { notification ->
            val delay = (notification.triggerAtMillis - now).coerceAtLeast(0L)
            val request = OneTimeWorkRequestBuilder<OdometerReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        OdometerReminderWorker.KEY_VEHICLE_ID to notification.vehicleId,
                        OdometerReminderWorker.KEY_TITLE to notification.title,
                        OdometerReminderWorker.KEY_BODY to notification.body,
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
        const val TAG = "odometer_reminder_notification"
    }
}
