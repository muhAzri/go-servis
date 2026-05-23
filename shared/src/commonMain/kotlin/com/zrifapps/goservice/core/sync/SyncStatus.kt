package com.zrifapps.goservice.core.sync

enum class SyncStatus {
    PendingCreate,
    PendingUpdate,
    PendingDelete,
    Synced,
    Failed,
}
