package com.zrifapps.goservice.feature.profile.data.local

import com.zrifapps.goservice.core.database.toDomain
import com.zrifapps.goservice.core.database.toEmbed
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.profile.domain.model.Profile

private const val DEFAULT_AVATAR_HEX = "#2E8B57"

fun ProfileEntity.toDomain(): Profile = Profile(
    id = id,
    name = name,
    avatarColor = HexColor.parseOrNull(avatarColorHex) ?: HexColor(DEFAULT_AVATAR_HEX),
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toDomain(),
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    id = id,
    name = name,
    avatarColorHex = avatarColor.value,
    email = email,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toEmbed(),
)
