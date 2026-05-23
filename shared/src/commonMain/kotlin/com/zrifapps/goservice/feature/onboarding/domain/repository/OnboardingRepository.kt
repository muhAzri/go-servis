package com.zrifapps.goservice.feature.onboarding.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {

    fun observe(): Flow<OnboardingState>

    suspend fun get(): DomainResult<OnboardingState>

    suspend fun advance(to: OnboardingStep): DomainResult<OnboardingState>

    suspend fun setProfileName(name: String): DomainResult<OnboardingState>

    suspend fun setVehicleType(type: String): DomainResult<OnboardingState>

    suspend fun setFirstVehicleId(id: String): DomainResult<OnboardingState>

    suspend fun recordNotificationPermission(
        asked: Boolean,
        granted: Boolean,
    ): DomainResult<OnboardingState>

    suspend fun complete(): DomainResult<OnboardingState>

    suspend fun reset(): DomainResult<Unit>
}
