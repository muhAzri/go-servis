package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.core.database.DatabaseBuilderFactory
import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.core.database.build
import com.zrifapps.goservice.core.notification.IosOdometerReminderScheduler
import com.zrifapps.goservice.core.notification.IosReminderNotificationScheduler
import com.zrifapps.goservice.core.notification.OdometerReminderScheduler
import com.zrifapps.goservice.core.notification.ReminderNotificationScheduler
import com.zrifapps.goservice.feature.feedback.data.remote.IosFeedbackRepository
import com.zrifapps.goservice.feature.feedback.domain.repository.FeedbackRepository
import com.zrifapps.goservice.feature.settings.data.local.SettingsStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseBuilderFactory() }
    single<GoServiceDatabase> { get<DatabaseBuilderFactory>().build() }
    single { SettingsStore() }
    single<ReminderNotificationScheduler> { IosReminderNotificationScheduler() }
    single<OdometerReminderScheduler> { IosOdometerReminderScheduler() }
    single<FeedbackRepository> { IosFeedbackRepository() }
}
