package com.zrifapps.goservice.feature.vehicle.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    @SerialName("id") val id: String,
    @SerialName("owner_profile_id") val ownerProfileId: String,
    @SerialName("nickname") val nickname: String,
    @SerialName("type") val type: String,
    @SerialName("subtype_id") val subtypeId: String,
    @SerialName("brand") val brand: String,
    @SerialName("model") val model: String,
    @SerialName("year") val year: Int? = null,
    @SerialName("plate_number") val plateNumber: String,
    @SerialName("odometer_km") val odometerKm: Long,
    @SerialName("color_hex") val colorHex: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
    @SerialName("last_odometer_update_at") val lastOdometerUpdateAt: Long = 0L,
    @SerialName("deleted_at") val deletedAt: Long? = null,
    @SerialName("version") val version: Long = 0L,
)

@Serializable
data class VehicleListResponseDto(
    @SerialName("items") val items: List<VehicleDto>,
    @SerialName("total") val total: Int,
)
