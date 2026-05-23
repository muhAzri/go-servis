package com.zrifapps.goservice.feature.component.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.data.dto.ComponentDto
import com.zrifapps.goservice.feature.component.data.dto.ComponentIntervalDto
import com.zrifapps.goservice.feature.component.data.dto.TrackedComponentDto
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentInterval
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent

fun ComponentIntervalDto.toDomain(): ComponentInterval = ComponentInterval(
    distance = Distance.ofKmOrNull(km),
    durationDays = durationDays,
    displayLabel = displayLabel,
)

fun ComponentInterval.toDto(): ComponentIntervalDto = ComponentIntervalDto(
    km = distance?.kilometers,
    durationDays = durationDays,
    displayLabel = displayLabel,
)

fun ComponentDto.toDomain(): Component = Component(
    id = id,
    label = label,
    iconKey = iconKey,
    colorHex = colorHex,
    intervalMotor = intervalMotor?.toDomain(),
    intervalMobil = intervalMobil?.toDomain(),
    why = why,
    applicableSubtypes = applicableSubtypes,
    tag = ComponentTag.fromKey(tag),
    isCustom = isCustom,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Component.toDto(): ComponentDto = ComponentDto(
    id = id,
    label = label,
    iconKey = iconKey,
    colorHex = colorHex,
    intervalMotor = intervalMotor?.toDto(),
    intervalMobil = intervalMobil?.toDto(),
    why = why,
    applicableSubtypes = applicableSubtypes,
    tag = tag.key,
    isCustom = isCustom,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TrackedComponentDto.toDomain(): TrackedComponent = TrackedComponent(
    id = id,
    vehicleId = vehicleId,
    catalogComponentId = catalogComponentId,
    customName = customName,
    lastServiceDate = lastServiceDate,
    lastServiceOdometer = Distance.ofKmOrNull(lastServiceKm),
    nextServiceDate = nextServiceDate,
    nextServiceOdometer = Distance.ofKmOrNull(nextServiceKm),
    intervalKmOverride = intervalKmOverride,
    intervalDaysOverride = intervalDaysOverride,
    urgency = urgency.toUrgency(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = SyncMetadata(
        status = if (deletedAt != null) SyncStatus.PendingDelete else SyncStatus.Synced,
        localUpdatedAt = updatedAt,
        remoteUpdatedAt = updatedAt,
        deletedAt = deletedAt,
        version = version,
    ),
)

fun TrackedComponent.toDto(): TrackedComponentDto = TrackedComponentDto(
    id = id,
    vehicleId = vehicleId,
    catalogComponentId = catalogComponentId,
    customName = customName,
    lastServiceDate = lastServiceDate,
    lastServiceKm = lastServiceOdometer?.kilometers,
    nextServiceDate = nextServiceDate,
    nextServiceKm = nextServiceOdometer?.kilometers,
    intervalKmOverride = intervalKmOverride,
    intervalDaysOverride = intervalDaysOverride,
    urgency = urgency.name.lowercase(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)

private fun String.toUrgency(): ComponentUrgency = when (lowercase()) {
    "overdue" -> ComponentUrgency.Overdue
    "soon" -> ComponentUrgency.Soon
    else -> ComponentUrgency.Ok
}
