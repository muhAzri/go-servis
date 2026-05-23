package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.id.UuidIdGenerator
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.time.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val APP_COROUTINE_SCOPE = named("AppCoroutineScope")

val coreModule = module {
    single<AppClock> { SystemClock() }
    single<IdGenerator> { UuidIdGenerator() }
    single<CoroutineScope>(APP_COROUTINE_SCOPE) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
