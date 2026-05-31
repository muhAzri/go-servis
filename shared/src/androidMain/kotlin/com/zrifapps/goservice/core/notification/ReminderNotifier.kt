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
 * Builds and posts the actual Android notification for a reminder occurrence,
 * including the tap deep-link (opens the reminder) and an inline snooze action.
 */
object ReminderNotifier {

    const val CHANNEL_ID: String = "service_reminders"
    const val EXTRA_REMINDER_ID: String = "extra_reminder_id"
    const val EXTRA_NOTIFICATION_ID: String = "extra_notification_id"
    const val ACTION_SNOOZE: String = "com.zrifapps.goservice.action.SNOOZE_REMINDER"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat servis",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Pengingat jadwal servis komponen kendaraan"
        }
        manager.createNotificationChannel(channel)
    }

    fun show(context: Context, reminderId: String, title: String, body: String) {
        ensureChannel(context)
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val notificationId = reminderId.hashCode()

        val openIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_REMINDER_ID, reminderId)
            }
        val contentIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent ?: Intent(),
            pendingIntentFlags(),
        )

        val snoozeIntent = Intent(context, ReminderSnoozeReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
        val snoozeAction = PendingIntent.getBroadcast(
            context,
            notificationId,
            snoozeIntent,
            pendingIntentFlags(),
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .addAction(0, "Tunda 3 hari", snoozeAction)
            .build()

        try {
            manager.notify(notificationId, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS revoked between the check and the post; ignore.
        }
    }

    private fun pendingIntentFlags(): Int =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}
