// DEFERRED: Tip feature is hidden for MVP. Content will come from a network API
// in a later release. Do NOT build new features on top of this layer yet.
package com.zrifapps.goservice.feature.tip.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipDto(
    @SerialName("id") val id: String,
    @SerialName("category_key") val categoryKey: String,
    @SerialName("title") val title: String,
    @SerialName("excerpt") val excerpt: String,
    @SerialName("body") val body: String,
    @SerialName("read_minutes") val readMinutes: Int,
    @SerialName("author") val author: String? = null,
    @SerialName("published_at") val publishedAt: Long,
    @SerialName("featured") val featured: Boolean = false,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)

@Serializable
data class TipCategoryDto(
    @SerialName("key") val key: String,
    @SerialName("label") val label: String,
)
