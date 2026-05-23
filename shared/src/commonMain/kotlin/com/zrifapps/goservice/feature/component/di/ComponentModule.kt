package com.zrifapps.goservice.feature.component.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.TrackedComponentDao
import com.zrifapps.goservice.feature.component.data.repository.ComponentCatalogRepositoryImpl
import com.zrifapps.goservice.feature.component.data.repository.TrackedComponentRepositoryImpl
import com.zrifapps.goservice.feature.component.data.seed.ComponentCatalogSeeder
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalog
import com.zrifapps.goservice.feature.component.domain.usecase.TrackComponent
import com.zrifapps.goservice.feature.component.domain.usecase.UntrackComponent
import org.koin.dsl.module

val componentModule = module {
    single<ComponentDao> { get<GoServiceDatabase>().componentDao() }
    single<TrackedComponentDao> { get<GoServiceDatabase>().trackedComponentDao() }
    single<ComponentCatalogRepository> { ComponentCatalogRepositoryImpl(get(), get()) }
    single<TrackedComponentRepository> { TrackedComponentRepositoryImpl(get(), get(), get()) }
    single { ComponentCatalogSeeder(get(), get()) }

    factory { ObserveComponentCatalog(get()) }
    factory { TrackComponent(get()) }
    factory { UntrackComponent(get()) }
}
