package com.zrifapps.goservice.feature.onboarding.presentation

import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep

sealed interface AppGate {
    data object Loading : AppGate
    data class Onboarding(val resumeStep: OnboardingStep) : AppGate
    data object Main : AppGate
}
