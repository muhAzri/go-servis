package com.zrifapps.goservice.feature.service.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.service.data.local.ServiceRecordDao
import com.zrifapps.goservice.feature.service.data.repository.ServiceRepositoryImpl
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import com.zrifapps.goservice.feature.service.domain.usecase.DeleteServiceRecord
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveServiceHistory
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveVehicleServiceHistory
import com.zrifapps.goservice.feature.service.domain.usecase.RecordService
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val serviceModule = module {
    single<ServiceRecordDao> { get<GoServiceDatabase>().serviceRecordDao() }
    single<ServiceRepository> { ServiceRepositoryImpl(get(), get(), get()) }

    factory { ObserveServiceHistory(get()) }
    factory { ObserveVehicleServiceHistory(get()) }
    factory { RecordService(get(), get()) }
    factory { DeleteServiceRecord(get()) }

    viewModel { ServiceHistoryViewModel(get()) }
}
