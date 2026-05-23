package com.zrifapps.goservice.feature.onboarding.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.onboarding.data.local.OnboardingStateDao
import com.zrifapps.goservice.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.zrifapps.goservice.feature.onboarding.domain.repository.OnboardingRepository
import com.zrifapps.goservice.feature.onboarding.domain.usecase.AdvanceOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.CompleteOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.ObserveOnboarding
import com.zrifapps.goservice.feature.onboarding.domain.usecase.RecordNotificationPermission
import com.zrifapps.goservice.feature.onboarding.domain.usecase.SetOnboardingProfileName
import org.koin.dsl.module

val onboardingModule = module {
    single<OnboardingStateDao> { get<GoServiceDatabase>().onboardingStateDao() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get(), get()) }

    factory { ObserveOnboarding(get()) }
    factory { AdvanceOnboarding(get()) }
    factory { SetOnboardingProfileName(get()) }
    factory { RecordNotificationPermission(get()) }
    factory { CompleteOnboarding(get()) }
}
