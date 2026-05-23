package com.zrifapps.goservice.feature.reminder.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReminderDto(
    @SerialName("id") val id: String,
    @SerialName("vehicle_id") val vehicleId: String,
    @SerialName("service_type") val serviceType: String,
    @SerialName("tracked_component_id") val trackedComponentId: String? = null,
    @SerialName("title") val title: String,
    @SerialName("trigger_mode") val triggerMode: String,
    @SerialName("target_km") val targetKm: Long? = null,
    @SerialName("target_date") val targetDate: Long? = null,
    @SerialName("note") val note: String? = null,
    @SerialName("notify_days_before") val notifyDaysBefore: Int = 7,
    @SerialName("status") val status: String,
    @SerialName("urgency") val urgency: String,
    @SerialName("snoozed_until") val snoozedUntil: Long? = null,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("completed_service_record_id") val completedServiceRecordId: String? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)
