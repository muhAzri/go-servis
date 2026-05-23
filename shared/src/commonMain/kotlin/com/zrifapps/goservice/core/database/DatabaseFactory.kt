package com.zrifapps.goservice.core.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect class DatabaseBuilderFactory {
    fun create(): RoomDatabase.Builder<GoServiceDatabase>
}

fun DatabaseBuilderFactory.build(): GoServiceDatabase =
    create()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .build()
