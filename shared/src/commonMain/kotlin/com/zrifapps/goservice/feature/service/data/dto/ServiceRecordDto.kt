package com.zrifapps.goservice.feature.service.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRecordDto(
    @SerialName("id") val id: String,
    @SerialName("vehicle_id") val vehicleId: String,
    @SerialName("service_type") val serviceType: String,
    @SerialName("service_date") val serviceDate: Long,
    @SerialName("odometer_km") val odometerKm: Long,
    @SerialName("workshop") val workshop: String? = null,
    @SerialName("cost_idr") val costIdr: Long,
    @SerialName("note") val note: String? = null,
    @SerialName("component_ids") val componentIds: List<String> = emptyList(),
    @SerialName("source_reminder_id") val sourceReminderId: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)
