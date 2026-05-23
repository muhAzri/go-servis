package com.zrifapps.goservice.feature.profile.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.model.ProfileDraft
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository

class EnsureProfileSeeded(
    private val repository: ProfileRepository,
) : UseCase<EnsureProfileSeeded.Params, Profile> {

    data class Params(
        val name: String? = null,
        val avatarColor: HexColor = DEFAULT_AVATAR_COLOR,
    )

    override suspend fun invoke(params: Params): DomainResult<Profile> {
        val existing = repository.getCurrent()
        if (existing is DomainResult.Failure) return existing
        val current = (existing as DomainResult.Success).data
        if (current != null) return DomainResult.Success(current)

        val name = params.name?.trim()?.takeIf(String::isNotEmpty) ?: DEFAULT_NAME
        return repository.createIfMissing(
            ProfileDraft(name = name, avatarColor = params.avatarColor),
        )
    }

    companion object {
        const val DEFAULT_NAME: String = "Kamu"
        val DEFAULT_AVATAR_COLOR: HexColor = HexColor("#2E8B57")
    }
}
