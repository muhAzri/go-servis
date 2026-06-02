package com.zrifapps.goservice.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Builds and posts the "update KM" notification for a single vehicle. Uses its
 * own notification channel so the user can mute these without losing service
 * reminders.
 */
object OdometerNotifier {

    const val CHANNEL_ID: String = "odometer_reminders"
    const val EXTRA_VEHICLE_ID: String = "extra_odometer_vehicle_id"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Update KM",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Pengingat berkala untuk update kilometer kendaraan."
        }
        manager.createNotificationChannel(channel)
    }

    fun show(context: Context, vehicleId: String, title: String, body: String) {
        ensureChannel(context)
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val notificationId = ("odo:" + vehicleId).hashCode()

        val openIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_VEHICLE_ID, vehicleId)
            }
        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent ?: Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        try {
            manager.notify(notificationId, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS revoked between check and post; ignore.
        }
    }
}
