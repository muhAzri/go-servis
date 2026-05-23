package com.zrifapps.goservice.feature.onboarding.data.local

import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep

fun OnboardingStateEntity.toDomain(): OnboardingState = OnboardingState(
    completed = completed,
    completedAt = completedAt,
    currentStep = runCatching { OnboardingStep.valueOf(currentStep) }.getOrDefault(OnboardingStep.Welcome),
    profileName = profileName,
    pickedVehicleType = pickedVehicleType,
    firstVehicleId = firstVehicleId,
    notificationPermissionAsked = notificationPermissionAsked,
    notificationPermissionGranted = notificationPermissionGranted,
)

fun OnboardingState.toEntity(): OnboardingStateEntity = OnboardingStateEntity(
    id = OnboardingStateEntity.SINGLETON_ID,
    completed = completed,
    completedAt = completedAt,
    currentStep = currentStep.name,
    profileName = profileName,
    pickedVehicleType = pickedVehicleType,
    firstVehicleId = firstVehicleId,
    notificationPermissionAsked = notificationPermissionAsked,
    notificationPermissionGranted = notificationPermissionGranted,
)
