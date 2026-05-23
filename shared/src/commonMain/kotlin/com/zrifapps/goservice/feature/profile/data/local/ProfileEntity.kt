package com.zrifapps.goservice.feature.profile.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zrifapps.goservice.core.database.SyncMetadataEmbed

@Entity(
    tableName = "profiles",
    indices = [Index("sync_deleted_at")],
)
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo("avatar_color_hex") val avatarColorHex: String,
    val email: String?,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @Embedded(prefix = "sync_") val sync: SyncMetadataEmbed,
)
