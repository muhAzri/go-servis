package com.zrifapps.goservice.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.zrifapps.goservice.feature.reminder.domain.usecase.SnoozeReminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

/** Handles the inline "Tunda 3 hari" notification action. */
class ReminderSnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ReminderNotifier.ACTION_SNOOZE) return
        val reminderId = intent.getStringExtra(ReminderNotifier.EXTRA_REMINDER_ID) ?: return
        val notificationId = intent.getIntExtra(
            ReminderNotifier.EXTRA_NOTIFICATION_ID,
            reminderId.hashCode(),
        )
        NotificationManagerCompat.from(context).cancel(notificationId)

        val pending = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val koin = KoinPlatform.getKoin()
                koin.get<SnoozeReminder>().invoke(
                    SnoozeReminder.Params(reminderId, SnoozeReminder.SnoozeDuration.ThreeDays),
                )
                koin.get<RescheduleReminderNotifications>().invoke()
            } finally {
                pending.finish()
            }
        }
    }
}
