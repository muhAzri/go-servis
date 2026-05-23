package com.zrifapps.goservice.feature.profile.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.id.IdGenerator
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.profile.data.local.ProfileDao
import com.zrifapps.goservice.feature.profile.data.local.toDomain
import com.zrifapps.goservice.feature.profile.data.local.toEntity
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.model.ProfileDraft
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val dao: ProfileDao,
    private val idGenerator: IdGenerator,
    private val clock: AppClock,
) : ProfileRepository {

    override fun observeCurrent(): Flow<Profile?> =
        dao.observeCurrent().map { it?.toDomain() }

    override suspend fun getCurrent(): DomainResult<Profile?> {
        val entity = dao.getCurrent() ?: return DomainResult.Success(null)
        return DomainResult.Success(entity.toDomain())
    }

    override suspend fun createIfMissing(draft: ProfileDraft): DomainResult<Profile> {
        val existing = dao.getCurrent()
        if (existing != null) return DomainResult.Success(existing.toDomain())
        val now = clock.nowEpochMillis()
        val profile = Profile(
            id = idGenerator.newId(),
            name = draft.name,
            avatarColor = draft.avatarColor,
            email = draft.email,
            createdAt = now,
            updatedAt = now,
            sync = SyncMetadata.newLocal(now),
        )
        return runStorage {
            dao.upsert(profile.toEntity())
            profile
        }
    }

    override suspend fun update(profile: Profile): DomainResult<Profile> {
        val now = clock.nowEpochMillis()
        val updated = profile.copy(
            updatedAt = now,
            sync = profile.sync.copy(
                status = SyncStatus.PendingUpdate,
                localUpdatedAt = now,
                version = profile.sync.version + 1,
            ),
        )
        return runStorage {
            dao.upsert(updated.toEntity())
            updated
        }
    }

    override suspend fun reset(): DomainResult<Unit> = runStorage {
        dao.clear()
    }

    override suspend fun refresh(): DomainResult<Unit> = DomainResult.Success(Unit)
}
