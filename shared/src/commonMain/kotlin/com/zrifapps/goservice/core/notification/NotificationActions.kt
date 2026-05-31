package com.zrifapps.goservice.core.notification

import com.zrifapps.goservice.core.di.APP_COROUTINE_SCOPE
import com.zrifapps.goservice.feature.reminder.domain.usecase.SnoozeReminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

/**
 * Fire-and-forget bridge for platform notification handlers (e.g. iOS Swift
 * delegate) to mutate reminder state and re-sync scheduled notifications without
 * dealing with coroutines or DI directly.
 */
object NotificationActions {

    fun snooze(reminderId: String, days: Int = SnoozeReminder.SnoozeDuration.ThreeDays.days) {
        val koin = KoinPlatform.getKoin()
        val scope: CoroutineScope = koin.get(APP_COROUTINE_SCOPE)
        scope.launch {
            val duration = SnoozeReminder.SnoozeDuration.entries
                .firstOrNull { it.days == days } ?: SnoozeReminder.SnoozeDuration.ThreeDays
            koin.get<SnoozeReminder>().invoke(SnoozeReminder.Params(reminderId, duration))
            koin.get<RescheduleReminderNotifications>().invoke()
        }
    }

    fun reschedule() {
        val koin = KoinPlatform.getKoin()
        val scope: CoroutineScope = koin.get(APP_COROUTINE_SCOPE)
        scope.launch {
            koin.get<RescheduleReminderNotifications>().invoke()
        }
    }
}
