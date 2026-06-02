package com.zrifapps.goservice.core.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class OdometerReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : Worker(appContext, params) {

    override fun doWork(): Result {
        val vehicleId = inputData.getString(KEY_VEHICLE_ID) ?: return Result.success()
        val title = inputData.getString(KEY_TITLE) ?: "Update KM"
        val body = inputData.getString(KEY_BODY).orEmpty()
        OdometerNotifier.show(applicationContext, vehicleId, title, body)
        return Result.success()
    }

    companion object {
        const val KEY_VEHICLE_ID: String = "vehicle_id"
        const val KEY_TITLE: String = "title"
        const val KEY_BODY: String = "body"
    }
}
