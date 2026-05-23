package com.zrifapps.goservice.feature.service.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "service_record_component_xref",
    primaryKeys = ["service_record_id", "component_id"],
    foreignKeys = [
        ForeignKey(
            entity = ServiceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["service_record_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("service_record_id"),
        Index("component_id"),
    ],
)
data class ServiceRecordComponentCrossRef(
    @ColumnInfo("service_record_id") val serviceRecordId: String,
    @ColumnInfo("component_id") val componentId: String,
)
