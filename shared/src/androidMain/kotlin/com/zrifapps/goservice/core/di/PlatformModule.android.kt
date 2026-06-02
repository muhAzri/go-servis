package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.core.database.DatabaseBuilderFactory
import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.core.database.build
import com.zrifapps.goservice.core.notification.AndroidOdometerReminderScheduler
import com.zrifapps.goservice.core.notification.AndroidReminderNotificationScheduler
import com.zrifapps.goservice.core.notification.OdometerReminderScheduler
import com.zrifapps.goservice.core.notification.ReminderNotificationScheduler
import com.zrifapps.goservice.feature.settings.data.local.SettingsStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseBuilderFactory(androidContext()) }
    single<GoServiceDatabase> { get<DatabaseBuilderFactory>().build() }
    single { SettingsStore(androidContext()) }
    single<ReminderNotificationScheduler> { AndroidReminderNotificationScheduler(androidContext()) }
    single<OdometerReminderScheduler> { AndroidOdometerReminderScheduler(androidContext()) }
}
