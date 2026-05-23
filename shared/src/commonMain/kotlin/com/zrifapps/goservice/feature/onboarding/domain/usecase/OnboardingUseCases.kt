package com.zrifapps.goservice.feature.onboarding.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import com.zrifapps.goservice.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class ObserveOnboarding(
    private val repository: OnboardingRepository,
) {
    operator fun invoke(): Flow<OnboardingState> = repository.observe()
}

class AdvanceOnboarding(
    private val repository: OnboardingRepository,
) : UseCase<OnboardingStep, OnboardingState> {
    override suspend fun invoke(params: OnboardingStep): DomainResult<OnboardingState> =
        repository.advance(params)
}

class SetOnboardingProfileName(
    private val repository: OnboardingRepository,
) : UseCase<String, OnboardingState> {
    override suspend fun invoke(params: String): DomainResult<OnboardingState> {
        val trimmed = params.trim()
        if (trimmed.isEmpty()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("name"))
        }
        return repository.setProfileName(trimmed)
    }
}

class RecordNotificationPermission(
    private val repository: OnboardingRepository,
) : UseCase<RecordNotificationPermission.Params, OnboardingState> {
    data class Params(val asked: Boolean, val granted: Boolean)
    override suspend fun invoke(params: Params): DomainResult<OnboardingState> =
        repository.recordNotificationPermission(params.asked, params.granted)
}

class CompleteOnboarding(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): DomainResult<OnboardingState> = repository.complete()
}
