// DEFERRED: Tip feature is hidden for MVP. Content will come from a network API
// in a later release. Do NOT build new features on top of this layer yet.
package com.zrifapps.goservice.feature.tip.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.feature.tip.data.dto.TipCategoryDto
import com.zrifapps.goservice.feature.tip.data.dto.TipDto
import com.zrifapps.goservice.feature.tip.domain.model.Tip
import com.zrifapps.goservice.feature.tip.domain.model.TipCategory

fun TipDto.toDomain(): Tip = Tip(
    id = id,
    categoryKey = categoryKey,
    title = title,
    excerpt = excerpt,
    body = body,
    readMinutes = readMinutes,
    author = author,
    publishedAt = publishedAt,
    featured = featured,
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

fun Tip.toDto(): TipDto = TipDto(
    id = id,
    categoryKey = categoryKey,
    title = title,
    excerpt = excerpt,
    body = body,
    readMinutes = readMinutes,
    author = author,
    publishedAt = publishedAt,
    featured = featured,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)

fun TipCategoryDto.toDomain(): TipCategory = TipCategory(key = key, label = label)
fun TipCategory.toDto(): TipCategoryDto = TipCategoryDto(key = key, label = label)
