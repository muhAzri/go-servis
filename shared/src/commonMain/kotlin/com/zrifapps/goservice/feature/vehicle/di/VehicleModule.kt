package com.zrifapps.goservice.feature.vehicle.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleDao
import com.zrifapps.goservice.feature.vehicle.data.repository.VehicleRepositoryImpl
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import com.zrifapps.goservice.feature.vehicle.domain.usecase.AddVehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.DeleteVehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.GetVehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import com.zrifapps.goservice.feature.vehicle.domain.usecase.UpdateOdometer
import com.zrifapps.goservice.feature.vehicle.domain.usecase.UpdateVehicle
import com.zrifapps.goservice.feature.vehicle.presentation.AddVehicleViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.EditVehicleViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.UpdateOdometerViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleDetailViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val vehicleModule = module {
    single<VehicleDao> { get<GoServiceDatabase>().vehicleDao() }
    single<VehicleRepository> { VehicleRepositoryImpl(get(), get(), get()) }

    factory { ObserveVehicles(get()) }
    factory { GetVehicle(get()) }
    factory { AddVehicle(get()) }
    factory { UpdateVehicle(get()) }
    factory { UpdateOdometer(get()) }
    factory { DeleteVehicle(get()) }

    viewModel { VehicleListViewModel(get()) }
    viewModel { AddVehicleViewModel(get(), get()) }
    viewModel { EditVehicleViewModel(get(), get(), get(), get()) }
    viewModel { UpdateOdometerViewModel(get(), get()) }
    viewModel { VehicleDetailViewModel(get(), get()) }
}
