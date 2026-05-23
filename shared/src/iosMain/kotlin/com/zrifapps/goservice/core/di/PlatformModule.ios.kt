package com.zrifapps.goservice.core.di

import com.zrifapps.goservice.core.database.DatabaseBuilderFactory
import com.zrifapps.goservice.core.database.GoServiceDatabase
import com.zrifapps.goservice.core.database.build
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseBuilderFactory() }
    single<GoServiceDatabase> { get<DatabaseBuilderFactory>().build() }
}
