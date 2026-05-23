package com.zrifapps.goservice.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("avatar_color_hex") val avatarColorHex: String,
    @SerialName("email") val email: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)
