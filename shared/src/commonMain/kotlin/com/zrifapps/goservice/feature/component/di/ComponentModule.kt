package com.zrifapps.goservice.feature.component.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.TrackedComponentDao
import com.zrifapps.goservice.feature.component.data.repository.ComponentCatalogRepositoryImpl
import com.zrifapps.goservice.feature.component.data.repository.TrackedComponentRepositoryImpl
import com.zrifapps.goservice.feature.component.data.seed.ComponentCatalogSeeder
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.component.domain.usecase.GetCatalogComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalog
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalogAll
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.component.domain.usecase.ResolveComponentServiceCycle
import com.zrifapps.goservice.feature.component.domain.usecase.TrackComponent
import com.zrifapps.goservice.feature.component.domain.usecase.TrackComponentWithReminder
import com.zrifapps.goservice.feature.component.domain.usecase.UntrackComponent
import com.zrifapps.goservice.feature.component.domain.usecase.UpdateTrackedComponent
import com.zrifapps.goservice.feature.component.presentation.AddTrackedComponentViewModel
import com.zrifapps.goservice.feature.component.presentation.ComponentInfoViewModel
import com.zrifapps.goservice.feature.component.presentation.TrackedComponentDetailViewModel
import com.zrifapps.goservice.feature.component.presentation.VehicleComponentsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val componentModule = module {
    single<ComponentDao> { get<GoServiceDatabase>().componentDao() }
    single<TrackedComponentDao> { get<GoServiceDatabase>().trackedComponentDao() }
    single<ComponentCatalogRepository> { ComponentCatalogRepositoryImpl(get(), get()) }
    single<TrackedComponentRepository> { TrackedComponentRepositoryImpl(get(), get(), get()) }
    single { ComponentCatalogSeeder(get(), get()) }

    factory { ObserveComponentCatalog(get()) }
    factory { ObserveComponentCatalogAll(get()) }
    factory { GetCatalogComponent(get()) }
    factory { TrackComponent(get()) }
    factory { TrackComponentWithReminder(get(), get(), get(), get(), get()) }
    factory { ResolveComponentServiceCycle(get(), get(), get(), get(), get(), get()) }
    factory { UntrackComponent(get()) }
    factory { ObserveTrackedComponents(get()) }
    factory { ObserveTrackedComponent(get()) }
    factory { UpdateTrackedComponent(get()) }

    viewModel { VehicleComponentsViewModel(get(), get(), get()) }
    viewModel { TrackedComponentDetailViewModel(get(), get(), get(), get()) }
    viewModel { AddTrackedComponentViewModel(get(), get(), get()) }
    viewModel { ComponentInfoViewModel(get(), get(), get(), get(), get()) }
}
