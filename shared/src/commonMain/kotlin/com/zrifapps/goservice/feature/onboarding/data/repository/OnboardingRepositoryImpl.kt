package com.zrifapps.goservice.feature.onboarding.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.onboarding.data.local.OnboardingStateDao
import com.zrifapps.goservice.feature.onboarding.data.local.toDomain
import com.zrifapps.goservice.feature.onboarding.data.local.toEntity
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingState
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import com.zrifapps.goservice.feature.onboarding.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingRepositoryImpl(
    private val dao: OnboardingStateDao,
    private val clock: AppClock,
) : OnboardingRepository {

    override fun observe(): Flow<OnboardingState> =
        dao.observe().map { it?.toDomain() ?: OnboardingState.initial }

    override suspend fun get(): DomainResult<OnboardingState> {
        val entity = dao.get()
        return DomainResult.Success(entity?.toDomain() ?: OnboardingState.initial)
    }

    override suspend fun advance(to: OnboardingStep): DomainResult<OnboardingState> = mutate {
        it.copy(currentStep = to)
    }

    override suspend fun setProfileName(name: String): DomainResult<OnboardingState> = mutate {
        it.copy(profileName = name)
    }

    override suspend fun setVehicleType(type: String): DomainResult<OnboardingState> = mutate {
        it.copy(pickedVehicleType = type)
    }

    override suspend fun setFirstVehicleId(id: String): DomainResult<OnboardingState> = mutate {
        it.copy(firstVehicleId = id)
    }

    override suspend fun recordNotificationPermission(
        asked: Boolean,
        granted: Boolean,
    ): DomainResult<OnboardingState> = mutate {
        it.copy(
            notificationPermissionAsked = asked,
            notificationPermissionGranted = granted,
        )
    }

    override suspend fun complete(): DomainResult<OnboardingState> {
        val now = clock.nowEpochMillis()
        return mutate {
            it.copy(
                completed = true,
                completedAt = now,
                currentStep = OnboardingStep.Done,
            )
        }
    }

    override suspend fun reset(): DomainResult<Unit> = runStorage {
        dao.clear()
    }

    private suspend inline fun mutate(
        crossinline transform: (OnboardingState) -> OnboardingState,
    ): DomainResult<OnboardingState> = runStorage {
        val current = dao.get()?.toDomain() ?: OnboardingState.initial
        val next = transform(current)
        dao.upsert(next.toEntity())
        next
    }
}
