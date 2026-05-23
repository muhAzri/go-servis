// DEFERRED: Tip feature is hidden for MVP. Content will come from a network API
// in a later release. Do NOT build new features on top of this layer yet.
package com.zrifapps.goservice.feature.tip.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata

data class TipCategory(
    val key: String,
    val label: String,
)

data class Tip(
    val id: String,
    val categoryKey: String,
    val title: String,
    val excerpt: String,
    val body: String,
    val readMinutes: Int,
    val author: String?,
    val publishedAt: Long,
    val featured: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val sync: SyncMetadata,
)

data class TipFilter(
    val categoryKeys: Set<String> = emptySet(),
    val query: String? = null,
    val featuredOnly: Boolean = false,
)
