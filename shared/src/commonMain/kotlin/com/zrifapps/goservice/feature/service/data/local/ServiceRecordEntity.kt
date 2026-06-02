package com.zrifapps.goservice.feature.service.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zrifapps.goservice.core.database.SyncMetadataEmbed
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleEntity

@Entity(
    tableName = "service_records",
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
        Index("service_date"),
        Index("service_type"),
        Index("sync_deleted_at"),
    ],
)
data class ServiceRecordEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("vehicle_id") val vehicleId: String,
    @ColumnInfo("service_type") val serviceType: String,
    @ColumnInfo("kind", defaultValue = "komponen") val kind: String,
    @ColumnInfo("custom_title") val customTitle: String?,
    @ColumnInfo("service_date") val serviceDate: Long,
    @ColumnInfo("odometer_km") val odometerKm: Long,
    val workshop: String?,
    @ColumnInfo("cost_idr") val costIdr: Long,
    val note: String?,
    @ColumnInfo("source_reminder_id") val sourceReminderId: String?,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @Embedded(prefix = "sync_") val sync: SyncMetadataEmbed,
)
