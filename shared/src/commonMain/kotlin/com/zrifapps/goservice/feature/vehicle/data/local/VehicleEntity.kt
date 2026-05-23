package com.zrifapps.goservice.feature.vehicle.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zrifapps.goservice.core.database.SyncMetadataEmbed

@Entity(
    tableName = "vehicles",
    indices = [
        Index("owner_profile_id"),
        Index("sync_deleted_at"),
        Index("type"),
    ],
)
data class VehicleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("owner_profile_id") val ownerProfileId: String,
    val nickname: String,
    val type: String,
    @ColumnInfo("subtype_id") val subtypeId: String,
    val brand: String,
    val model: String,
    val year: Int?,
    @ColumnInfo("plate_number") val plateNumber: String,
    @ColumnInfo("odometer_km") val odometerKm: Long,
    @ColumnInfo("color_hex") val colorHex: String,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @Embedded(prefix = "sync_") val sync: SyncMetadataEmbed,
)
