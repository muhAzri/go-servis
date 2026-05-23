package com.zrifapps.goservice.feature.onboarding.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "onboarding_state")
data class OnboardingStateEntity(
    @PrimaryKey val id: String = SINGLETON_ID,
    val completed: Boolean,
    @ColumnInfo("completed_at") val completedAt: Long?,
    @ColumnInfo("current_step") val currentStep: String,
    @ColumnInfo("profile_name") val profileName: String?,
    @ColumnInfo("picked_vehicle_type") val pickedVehicleType: String?,
    @ColumnInfo("first_vehicle_id") val firstVehicleId: String?,
    @ColumnInfo("notification_permission_asked") val notificationPermissionAsked: Boolean,
    @ColumnInfo("notification_permission_granted") val notificationPermissionGranted: Boolean,
) {
    companion object {
        const val SINGLETON_ID: String = "onboarding"
    }
}
