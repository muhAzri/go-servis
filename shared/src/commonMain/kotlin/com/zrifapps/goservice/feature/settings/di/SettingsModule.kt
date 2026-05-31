package com.zrifapps.goservice.feature.settings.di

import com.zrifapps.goservice.feature.settings.data.repository.SettingsRepositoryImpl
import com.zrifapps.goservice.feature.settings.domain.repository.SettingsRepository
import com.zrifapps.goservice.feature.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    viewModel { SettingsViewModel(get()) }
}
