package com.zrifapps.goservice.feature.onboarding.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingStateDto(
    @SerialName("completed") val completed: Boolean,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("current_step") val currentStep: String,
    @SerialName("profile_name") val profileName: String? = null,
    @SerialName("picked_vehicle_type") val pickedVehicleType: String? = null,
    @SerialName("first_vehicle_id") val firstVehicleId: String? = null,
    @SerialName("notification_permission_asked") val notificationPermissionAsked: Boolean = false,
    @SerialName("notification_permission_granted") val notificationPermissionGranted: Boolean = false,
)
