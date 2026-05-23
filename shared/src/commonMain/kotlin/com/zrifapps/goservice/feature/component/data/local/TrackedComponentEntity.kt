package com.zrifapps.goservice.feature.component.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zrifapps.goservice.core.database.SyncMetadataEmbed
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleEntity

@Entity(
    tableName = "tracked_components",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicle_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("vehicle_id"),
        Index("catalog_component_id"),
        Index("urgency"),
        Index("sync_deleted_at"),
    ],
)
data class TrackedComponentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("vehicle_id") val vehicleId: String,
    @ColumnInfo("catalog_component_id") val catalogComponentId: String,
    @ColumnInfo("custom_name") val customName: String?,
    @ColumnInfo("last_service_date") val lastServiceDate: Long?,
    @ColumnInfo("last_service_km") val lastServiceKm: Long?,
    @ColumnInfo("next_service_date") val nextServiceDate: Long?,
    @ColumnInfo("next_service_km") val nextServiceKm: Long?,
    @ColumnInfo("interval_km_override") val intervalKmOverride: Long?,
    @ColumnInfo("interval_days_override") val intervalDaysOverride: Int?,
    val urgency: String,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @Embedded(prefix = "sync_") val sync: SyncMetadataEmbed,
)
