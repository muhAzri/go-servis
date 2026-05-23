package com.zrifapps.goservice.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseBuilderFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<GoServiceDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(GoServiceDatabase.DATABASE_NAME)
        return Room.databaseBuilder<GoServiceDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}
