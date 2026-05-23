package com.zrifapps.goservice.core.sync

data class SyncMetadata(
    val status: SyncStatus,
    val localUpdatedAt: Long,
    val remoteUpdatedAt: Long? = null,
    val lastSyncAttemptAt: Long? = null,
    val syncErrorMessage: String? = null,
    val deletedAt: Long? = null,
    val version: Long = 0L,
) {
    val isDeleted: Boolean get() = deletedAt != null
    val isDirty: Boolean get() = status != SyncStatus.Synced && !isDeleted
    val needsPush: Boolean get() = status == SyncStatus.PendingCreate ||
        status == SyncStatus.PendingUpdate ||
        status == SyncStatus.PendingDelete

    companion object {
        fun newLocal(now: Long): SyncMetadata = SyncMetadata(
            status = SyncStatus.PendingCreate,
            localUpdatedAt = now,
            version = 0L,
        )
    }
}
