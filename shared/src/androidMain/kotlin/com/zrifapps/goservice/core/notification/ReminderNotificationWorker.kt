package com.zrifapps.goservice.core.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/** Fired by WorkManager at the scheduled time to post a reminder notification. */
class ReminderNotificationWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val reminderId = inputData.getString(KEY_REMINDER_ID) ?: return Result.success()
        val title = inputData.getString(KEY_TITLE) ?: "Pengingat servis"
        val body = inputData.getString(KEY_BODY).orEmpty()
        ReminderNotifier.show(applicationContext, reminderId, title, body)
        return Result.success()
    }

    companion object {
        const val KEY_REMINDER_ID: String = "reminder_id"
        const val KEY_TITLE: String = "title"
        const val KEY_BODY: String = "body"
    }
}
