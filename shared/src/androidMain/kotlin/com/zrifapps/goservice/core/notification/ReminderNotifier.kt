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

    // Bumped to "_v2" because a channel's importance can't be raised in code once
    // it already exists on the device — a new id forces the high-importance channel.
    const val CHANNEL_ID: String = "service_reminders_v2"
    private const val LEGACY_CHANNEL_ID: String = "service_reminders"
    const val EXTRA_REMINDER_ID: String = "extra_reminder_id"
    const val EXTRA_NOTIFICATION_ID: String = "extra_notification_id"
    const val ACTION_SNOOZE: String = "com.zrifapps.goservice.action.SNOOZE_REMINDER"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        // Remove the old default-importance channel so users don't see a stale duplicate.
        manager.deleteNotificationChannel(LEGACY_CHANNEL_ID)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat servis",
            // IMPORTANCE_HIGH = pops up as a heads-up notification with sound.
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Pengingat jadwal servis komponen kendaraan"
            enableVibration(true)
            enableLights(true)
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
            // PRIORITY_HIGH + CATEGORY_REMINDER + defaults drive the heads-up popup
            // on Android 7 and below (Android 8+ takes this from the channel instead).
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
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
