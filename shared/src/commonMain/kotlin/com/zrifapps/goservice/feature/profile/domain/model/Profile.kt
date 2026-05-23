package com.zrifapps.goservice.feature.profile.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.value.HexColor

data class Profile(
    val id: String,
    val name: String,
    val avatarColor: HexColor,
    val email: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val sync: SyncMetadata,
) {
    val initial: String get() = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}

data class ProfileDraft(
    val name: String,
    val avatarColor: HexColor,
    val email: String? = null,
)
