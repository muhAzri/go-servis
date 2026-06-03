package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.core.notification.OdometerReminderPlanner
import com.zrifapps.goservice.core.notification.OdometerReminderScheduler
import com.zrifapps.goservice.core.notification.ReminderNotificationPlanner
import com.zrifapps.goservice.core.notification.ReminderNotificationScheduler
import com.zrifapps.goservice.core.notification.notificationModule
import com.zrifapps.goservice.feature.backup.di.backupModule
import com.zrifapps.goservice.feature.component.data.seed.ComponentCatalogSeeder
import com.zrifapps.goservice.feature.component.di.componentModule
import com.zrifapps.goservice.feature.feedback.di.feedbackModule
import com.zrifapps.goservice.feature.onboarding.di.onboardingModule
import com.zrifapps.goservice.feature.profile.di.profileModule
import com.zrifapps.goservice.feature.reminder.di.reminderModule
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.service.di.serviceModule
import com.zrifapps.goservice.feature.settings.di.settingsModule
import com.zrifapps.goservice.feature.settings.domain.repository.SettingsRepository
import com.zrifapps.goservice.feature.vehicle.di.vehicleModule
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

val appModules = listOf(
    platformModule,
    coreModule,
    vehicleModule,
    componentModule,
    serviceModule,
    reminderModule,
    profileModule,
    onboardingModule,
    settingsModule,
    notificationModule,
    backupModule,
    feedbackModule,
)

fun startAppKoin(extra: KoinApplication.() -> Unit = {}) = startKoin {
    extra()
    modules(appModules)
}

fun runStartupTasks() {
    val koin = KoinPlatform.getKoin()
    val scope: CoroutineScope = koin.get(APP_COROUTINE_SCOPE)
    scope.launch {
        koin.get<ComponentCatalogSeeder>().seedIfNeeded()
    }
    scope.launch {
        val reminderRepository = koin.get<ReminderRepository>()
        val settingsRepository = koin.get<SettingsRepository>()
        val planner = koin.get<ReminderNotificationPlanner>()
        val scheduler = koin.get<ReminderNotificationScheduler>()
        combine(
            reminderRepository.observe(
                ReminderFilter(statuses = setOf(ReminderStatus.Active, ReminderStatus.Snoozed)),
            ),
            settingsRepository.observe(),
        ) { reminders, settings ->
            reminders to settings.serviceReminderNotificationsEnabled
        }.distinctUntilChanged().collect { (reminders, enabled) ->
            if (enabled) {
                scheduler.replaceAll(planner.plan(reminders))
            } else {
                scheduler.cancelAll()
            }
        }
    }
    scope.launch {
        val vehicleRepository = koin.get<VehicleRepository>()
        val settingsRepository = koin.get<SettingsRepository>()
        val odoPlanner = koin.get<OdometerReminderPlanner>()
        val odoScheduler = koin.get<OdometerReminderScheduler>()
        combine(
            vehicleRepository.observeVehicles(),
            settingsRepository.observe(),
        ) { vehicles, settings ->
            vehicles to settings.odometerReminderNotificationsEnabled
        }.distinctUntilChanged().collect { (vehicles, enabled) ->
            if (enabled) {
                odoScheduler.replaceAll(odoPlanner.plan(vehicles))
            } else {
                odoScheduler.cancelAll()
            }
        }
    }
}

fun bootstrapForIos() {
    startAppKoin()
    runStartupTasks()
}
