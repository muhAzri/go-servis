package com.zrifapps.goservice.feature.vehicle.data.local

import com.zrifapps.goservice.core.database.toDomain
import com.zrifapps.goservice.core.database.toEmbed
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType

private const val DEFAULT_COLOR_HEX = "#1A2418"

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
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
    sync = sync.toDomain(),
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
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
    sync = sync.toEmbed(),
)
