package com.zrifapps.goservice.feature.profile.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.profile.data.dto.ProfileDto
import com.zrifapps.goservice.feature.profile.domain.model.Profile

private const val DEFAULT_AVATAR_HEX = "#2E8B57"

fun ProfileDto.toDomain(): Profile = Profile(
    id = id,
    name = name,
    avatarColor = HexColor.parseOrNull(avatarColorHex) ?: HexColor(DEFAULT_AVATAR_HEX),
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = SyncMetadata(
        status = if (deletedAt != null) SyncStatus.PendingDelete else SyncStatus.Synced,
        localUpdatedAt = updatedAt,
        remoteUpdatedAt = updatedAt,
        deletedAt = deletedAt,
        version = version,
    ),
)

fun Profile.toDto(): ProfileDto = ProfileDto(
    id = id,
    name = name,
    avatarColorHex = avatarColor.value,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)
