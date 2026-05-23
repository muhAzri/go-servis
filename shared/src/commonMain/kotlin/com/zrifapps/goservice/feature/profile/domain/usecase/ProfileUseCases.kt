package com.zrifapps.goservice.feature.profile.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.NoArgFlowUseCase
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.model.ProfileDraft
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveProfile(
    private val repository: ProfileRepository,
) : NoArgFlowUseCase<Profile?> {
    override fun invoke(): Flow<DomainResult<Profile?>> =
        repository.observeCurrent().map { DomainResult.Success(it) }
}

class UpdateProfile(
    private val repository: ProfileRepository,
) : UseCase<Profile, Profile> {

    override suspend fun invoke(params: Profile): DomainResult<Profile> {
        val nameError = validateName(params.name)
        if (nameError != null) return DomainResult.Failure(nameError)
        val emailError = params.email?.let(::validateEmail)
        if (emailError != null) return DomainResult.Failure(emailError)
        return repository.update(params)
    }
}

class CreateProfileIfMissing(
    private val repository: ProfileRepository,
) : UseCase<ProfileDraft, Profile> {

    override suspend fun invoke(params: ProfileDraft): DomainResult<Profile> {
        val nameError = validateName(params.name)
        if (nameError != null) return DomainResult.Failure(nameError)
        return repository.createIfMissing(params)
    }
}

class ResetProfile(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): DomainResult<Unit> = repository.reset()
}

private const val MAX_NAME_LEN = 20

private fun validateName(name: String): DomainError? {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return DomainError.Validation.FieldRequired("name")
    if (trimmed.length > MAX_NAME_LEN) {
        return DomainError.Validation.OutOfRange("name", "max $MAX_NAME_LEN chars")
    }
    return null
}

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

private fun validateEmail(email: String): DomainError? {
    val trimmed = email.trim()
    if (trimmed.isEmpty()) return null
    if (!emailRegex.matches(trimmed)) {
        return DomainError.Validation.InvalidFormat("email", "format email tidak valid")
    }
    return null
}
