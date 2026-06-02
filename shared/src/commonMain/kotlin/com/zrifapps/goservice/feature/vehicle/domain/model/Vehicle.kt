package com.zrifapps.goservice.feature.vehicle.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor

data class Vehicle(
    val id: String,
    val ownerProfileId: String,
    val nickname: String,
    val type: VehicleType,
    val subtypeId: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val plateNumber: String,
    val odometer: Distance,
    val color: HexColor,
    val createdAt: Long,
    val updatedAt: Long,
    /** Timestamp of the last odometer write (Add Vehicle or Update KM). Drives stale-KM reminders. */
    val lastOdometerUpdateAt: Long = createdAt,
    val sync: SyncMetadata,
) {
    val displayTitle: String get() = nickname.ifBlank { "$brand $model".trim() }
}
