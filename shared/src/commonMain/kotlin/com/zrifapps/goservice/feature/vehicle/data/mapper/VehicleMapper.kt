package com.zrifapps.goservice.feature.vehicle.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.vehicle.data.dto.VehicleDto
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType

private const val DEFAULT_COLOR_HEX = "#1A2418"

fun VehicleDto.toDomain(): Vehicle = Vehicle(
    id = id,
    ownerProfileId = ownerProfileId,
    nickname = nickname,
    type = VehicleType.fromKey(type),
    subtypeId = subtypeId,
    brand = brand,
    model = model,
    year = year,
    plateNumber = plateNumber,
    odometer = Distance.ofKm(odometerKm),
    color = HexColor.parseOrNull(colorHex) ?: HexColor(DEFAULT_COLOR_HEX),
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastOdometerUpdateAt = if (lastOdometerUpdateAt > 0L) lastOdometerUpdateAt else createdAt,
    sync = SyncMetadata(
        status = if (deletedAt != null) SyncStatus.PendingDelete else SyncStatus.Synced,
        localUpdatedAt = updatedAt,
        remoteUpdatedAt = updatedAt,
        deletedAt = deletedAt,
        version = version,
    ),
)

fun Vehicle.toDto(): VehicleDto = VehicleDto(
    id = id,
    ownerProfileId = ownerProfileId,
    nickname = nickname,
    type = type.key,
    subtypeId = subtypeId,
    brand = brand,
    model = model,
    year = year,
    plateNumber = plateNumber,
    odometerKm = odometer.kilometers,
    colorHex = color.value,
    createdAt = createdAt,
    updatedAt = updatedAt,
    lastOdometerUpdateAt = lastOdometerUpdateAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)
