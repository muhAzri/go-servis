package com.zrifapps.goservice.feature.onboarding.data.mapper

import com.zrifapps.goservice.feature.onboarding.data.dto.OnboardingStateDto
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep

fun OnboardingStateDto.toDomain(): OnboardingState = OnboardingState(
    completed = completed,
    completedAt = completedAt,
    currentStep = currentStep.toStep(),
    profileName = profileName,
    pickedVehicleType = pickedVehicleType,
    firstVehicleId = firstVehicleId,
    notificationPermissionAsked = notificationPermissionAsked,
    notificationPermissionGranted = notificationPermissionGranted,
)

fun OnboardingState.toDto(): OnboardingStateDto = OnboardingStateDto(
    completed = completed,
    completedAt = completedAt,
    currentStep = currentStep.name,
    profileName = profileName,
    pickedVehicleType = pickedVehicleType,
    firstVehicleId = firstVehicleId,
    notificationPermissionAsked = notificationPermissionAsked,
    notificationPermissionGranted = notificationPermissionGranted,
)

private fun String.toStep(): OnboardingStep =
    OnboardingStep.entries.firstOrNull { it.name.equals(this, ignoreCase = true) }
        ?: OnboardingStep.Welcome
