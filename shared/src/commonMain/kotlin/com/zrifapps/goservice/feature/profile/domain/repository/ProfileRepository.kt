package com.zrifapps.goservice.feature.profile.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.model.ProfileDraft
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeCurrent(): Flow<Profile?>

    suspend fun getCurrent(): DomainResult<Profile?>

    suspend fun createIfMissing(draft: ProfileDraft): DomainResult<Profile>

    suspend fun update(profile: Profile): DomainResult<Profile>

    suspend fun reset(): DomainResult<Unit>

    suspend fun refresh(): DomainResult<Unit>
}
