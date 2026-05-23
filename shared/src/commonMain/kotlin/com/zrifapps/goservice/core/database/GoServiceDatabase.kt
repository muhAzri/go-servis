package com.zrifapps.goservice.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.ComponentEntity
import com.zrifapps.goservice.feature.component.data.local.TrackedComponentDao
import com.zrifapps.goservice.feature.component.data.local.TrackedComponentEntity
import com.zrifapps.goservice.feature.onboarding.data.local.OnboardingStateDao
import com.zrifapps.goservice.feature.onboarding.data.local.OnboardingStateEntity
import com.zrifapps.goservice.feature.profile.data.local.ProfileDao
import com.zrifapps.goservice.feature.profile.data.local.ProfileEntity
import com.zrifapps.goservice.feature.reminder.data.local.ReminderDao
import com.zrifapps.goservice.feature.reminder.data.local.ReminderEntity
import com.zrifapps.goservice.feature.service.data.local.ServiceRecordComponentCrossRef
import com.zrifapps.goservice.feature.service.data.local.ServiceRecordDao
import com.zrifapps.goservice.feature.service.data.local.ServiceRecordEntity
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleDao
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        ComponentEntity::class,
        TrackedComponentEntity::class,
        ServiceRecordEntity::class,
        ServiceRecordComponentCrossRef::class,
        ReminderEntity::class,
        ProfileEntity::class,
        OnboardingStateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(RoomTypeConverters::class)
@ConstructedBy(GoServiceDatabaseConstructor::class)
abstract class GoServiceDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun componentDao(): ComponentDao
    abstract fun trackedComponentDao(): TrackedComponentDao
    abstract fun serviceRecordDao(): ServiceRecordDao
    abstract fun reminderDao(): ReminderDao
    abstract fun profileDao(): ProfileDao
    abstract fun onboardingStateDao(): OnboardingStateDao

    companion object {
        const val DATABASE_NAME: String = "goservice.db"
    }
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object GoServiceDatabaseConstructor : RoomDatabaseConstructor<GoServiceDatabase> {
    override fun initialize(): GoServiceDatabase
}
