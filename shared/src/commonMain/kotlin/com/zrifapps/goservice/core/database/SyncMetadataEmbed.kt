package com.zrifapps.goservice.core.database

import androidx.room.ColumnInfo
import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus

data class SyncMetadataEmbed(
    val status: String,
    @ColumnInfo("local_updated_at") val localUpdatedAt: Long,
    @ColumnInfo("remote_updated_at") val remoteUpdatedAt: Long?,
    @ColumnInfo("last_sync_attempt_at") val lastSyncAttemptAt: Long?,
    @ColumnInfo("sync_error_message") val syncErrorMessage: String?,
    @ColumnInfo("deleted_at") val deletedAt: Long?,
    val version: Long,
)

fun SyncMetadata.toEmbed(): SyncMetadataEmbed = SyncMetadataEmbed(
    status = status.name,
    localUpdatedAt = localUpdatedAt,
    remoteUpdatedAt = remoteUpdatedAt,
    lastSyncAttemptAt = lastSyncAttemptAt,
    syncErrorMessage = syncErrorMessage,
    deletedAt = deletedAt,
    version = version,
)

fun SyncMetadataEmbed.toDomain(): SyncMetadata = SyncMetadata(
    status = runCatching { SyncStatus.valueOf(status) }.getOrDefault(SyncStatus.Synced),
    localUpdatedAt = localUpdatedAt,
    remoteUpdatedAt = remoteUpdatedAt,
    lastSyncAttemptAt = lastSyncAttemptAt,
    syncErrorMessage = syncErrorMessage,
    deletedAt = deletedAt,
    version = version,
)
