package com.zrifapps.goservice.core.notification

import org.koin.dsl.module

/**
 * Platform-agnostic notification wiring. The actual [ReminderNotificationScheduler]
 * is bound per platform in the platform Koin module.
 */
val notificationModule = module {
    single { ReminderNotificationPlanner(get()) }
    factory { RescheduleReminderNotifications(get(), get(), get(), get()) }
    single { OdometerReminderPlanner(get()) }
}
