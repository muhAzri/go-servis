package com.zrifapps.goservice.feature.component.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComponentDto(
    @SerialName("id") val id: String,
    @SerialName("label") val label: String,
    @SerialName("icon_key") val iconKey: String,
    @SerialName("color_hex") val colorHex: String,
    @SerialName("interval_motor") val intervalMotor: ComponentIntervalDto? = null,
    @SerialName("interval_mobil") val intervalMobil: ComponentIntervalDto? = null,
    @SerialName("why") val why: String,
    @SerialName("applicable_subtypes") val applicableSubtypes: List<String>,
    @SerialName("tag") val tag: String,
    @SerialName("is_custom") val isCustom: Boolean = false,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)

@Serializable
data class ComponentIntervalDto(
    @SerialName("km") val km: Long? = null,
    @SerialName("duration_days") val durationDays: Int? = null,
    @SerialName("display_label") val displayLabel: String,
)

@Serializable
data class TrackedComponentDto(
    @SerialName("id") val id: String,
    @SerialName("vehicle_id") val vehicleId: String,
    @SerialName("catalog_component_id") val catalogComponentId: String,
    @SerialName("custom_name") val customName: String? = null,
    @SerialName("last_service_date") val lastServiceDate: Long? = null,
    @SerialName("last_service_km") val lastServiceKm: Long? = null,
    @SerialName("next_service_date") val nextServiceDate: Long? = null,
    @SerialName("next_service_km") val nextServiceKm: Long? = null,
    @SerialName("interval_km_override") val intervalKmOverride: Long? = null,
    @SerialName("interval_days_override") val intervalDaysOverride: Int? = null,
    @SerialName("urgency") val urgency: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)
