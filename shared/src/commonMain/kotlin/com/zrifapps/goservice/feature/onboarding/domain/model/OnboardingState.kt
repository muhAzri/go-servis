package com.zrifapps.goservice.feature.onboarding.domain.model

enum class OnboardingStep {
    Welcome, ProfileName, PickVehicleType, AddVehicle, NotificationPermission, Done;

    val nextOrNull: OnboardingStep? get() = entries.getOrNull(ordinal + 1)
}

data class OnboardingState(
    val completed: Boolean,
    val completedAt: Long?,
    val currentStep: OnboardingStep,
    val profileName: String?,
    val pickedVehicleType: String?,
    val firstVehicleId: String?,
    val notificationPermissionAsked: Boolean,
    val notificationPermissionGranted: Boolean,
) {
    companion object {
        val initial: OnboardingState = OnboardingState(
            completed = false,
            completedAt = null,
            currentStep = OnboardingStep.Welcome,
            profileName = null,
            pickedVehicleType = null,
            firstVehicleId = null,
            notificationPermissionAsked = false,
            notificationPermissionGranted = false,
        )
    }
}
