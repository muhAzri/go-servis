package com.zrifapps.goservice.feature.component.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "components",
    indices = [
        Index("tag"),
        Index("is_custom"),
    ],
)
data class ComponentEntity(
    @PrimaryKey val id: String,
    val label: String,
    @ColumnInfo("icon_key") val iconKey: String,
    @ColumnInfo("color_hex") val colorHex: String,
    @ColumnInfo("interval_motor_km") val intervalMotorKm: Long?,
    @ColumnInfo("interval_motor_days") val intervalMotorDays: Int?,
    @ColumnInfo("interval_motor_label") val intervalMotorLabel: String?,
    @ColumnInfo("interval_mobil_km") val intervalMobilKm: Long?,
    @ColumnInfo("interval_mobil_days") val intervalMobilDays: Int?,
    @ColumnInfo("interval_mobil_label") val intervalMobilLabel: String?,
    val why: String,
    @ColumnInfo("applicable_subtypes") val applicableSubtypes: List<String>,
    val tag: String,
    @ColumnInfo("is_custom") val isCustom: Boolean,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
)
