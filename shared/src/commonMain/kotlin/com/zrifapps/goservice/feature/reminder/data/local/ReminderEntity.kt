package com.zrifapps.goservice.feature.reminder.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zrifapps.goservice.core.database.SyncMetadataEmbed
import com.zrifapps.goservice.feature.vehicle.data.local.VehicleEntity

@Entity(
    tableName = "reminders",
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
        Index("status"),
        Index("urgency"),
        Index("sync_deleted_at"),
    ],
)
data class ReminderEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("vehicle_id") val vehicleId: String,
    @ColumnInfo("service_type") val serviceType: String,
    @ColumnInfo("tracked_component_id") val trackedComponentId: String?,
    val title: String,
    @ColumnInfo("trigger_mode") val triggerMode: String,
    @ColumnInfo("target_km") val targetKm: Long?,
    @ColumnInfo("target_date") val targetDate: Long?,
    val note: String?,
    @ColumnInfo("notify_days_before") val notifyDaysBefore: Int,
    val status: String,
    val urgency: String,
    @ColumnInfo("snoozed_until") val snoozedUntil: Long?,
    @ColumnInfo("completed_at") val completedAt: Long?,
    @ColumnInfo("completed_service_record_id") val completedServiceRecordId: String?,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @Embedded(prefix = "sync_") val sync: SyncMetadataEmbed,
)
