package com.zrifapps.goservice.feature.onboarding.presentation

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Welcome,
    val persistedName: String? = null,
    val nameInput: String = "",
    val pickedVehicleType: VehicleType? = null,
    val firstVehicleId: String? = null,
    val isSubmitting: Boolean = false,
    val error: DomainError? = null,
) {
    val effectiveName: String?
        get() = nameInput.trim().takeIf(String::isNotEmpty)
            ?: persistedName?.trim()?.takeIf(String::isNotEmpty)
}

data class OnboardingVehicleInput(
    val type: VehicleType,
    val nickname: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val plateNumber: String,
    val odometerKm: Long,
    val colorHex: String,
    val subtypeId: String? = null,
)

sealed interface OnboardingEvent {
    data class GoTo(val step: OnboardingStep) : OnboardingEvent
    data object CompletedFlow : OnboardingEvent
    data class Failed(val error: DomainError) : OnboardingEvent
}
