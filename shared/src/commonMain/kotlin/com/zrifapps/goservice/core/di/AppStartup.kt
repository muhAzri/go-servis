package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.feature.component.data.seed.ComponentCatalogSeeder
import com.zrifapps.goservice.feature.component.di.componentModule
import com.zrifapps.goservice.feature.onboarding.di.onboardingModule
import com.zrifapps.goservice.feature.profile.di.profileModule
import com.zrifapps.goservice.feature.reminder.di.reminderModule
import com.zrifapps.goservice.feature.service.di.serviceModule
import com.zrifapps.goservice.feature.settings.di.settingsModule
import com.zrifapps.goservice.feature.vehicle.di.vehicleModule
import kotlinx.coroutines.CoroutineScope
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
}

fun bootstrapForIos() {
    startAppKoin()
    runStartupTasks()
}
