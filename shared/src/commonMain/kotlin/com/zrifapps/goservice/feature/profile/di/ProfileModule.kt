package com.zrifapps.goservice.feature.profile.di

import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.feature.profile.data.local.ProfileDao
import com.zrifapps.goservice.feature.profile.data.repository.ProfileRepositoryImpl
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository
import com.zrifapps.goservice.feature.profile.domain.usecase.CreateProfileIfMissing
import com.zrifapps.goservice.feature.profile.domain.usecase.EnsureProfileSeeded
import com.zrifapps.goservice.feature.profile.domain.usecase.ObserveProfile
import com.zrifapps.goservice.feature.profile.domain.usecase.ResetProfile
import com.zrifapps.goservice.feature.profile.domain.usecase.UpdateProfile
import org.koin.dsl.module

val profileModule = module {
    single<ProfileDao> { get<GoServiceDatabase>().profileDao() }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }

    factory { ObserveProfile(get()) }
    factory { CreateProfileIfMissing(get()) }
    factory { EnsureProfileSeeded(get()) }
    factory { UpdateProfile(get()) }
    factory { ResetProfile(get()) }
}
