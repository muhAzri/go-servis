package com.zrifapps.goservice.feature.component.data.local

import com.zrifapps.goservice.core.database.toDomain
import com.zrifapps.goservice.core.database.toEmbed
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentInterval
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent

fun ComponentEntity.toDomain(): Component = Component(
    id = id,
    label = label,
    iconKey = iconKey,
    colorHex = colorHex,
    intervalMotor = buildInterval(intervalMotorKm, intervalMotorDays, intervalMotorLabel),
    intervalMobil = buildInterval(intervalMobilKm, intervalMobilDays, intervalMobilLabel),
    why = why,
    applicableSubtypes = applicableSubtypes,
    tag = ComponentTag.fromKey(tag),
    isCustom = isCustom,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Component.toEntity(): ComponentEntity = ComponentEntity(
    id = id,
    label = label,
    iconKey = iconKey,
    colorHex = colorHex,
    intervalMotorKm = intervalMotor?.distance?.kilometers,
    intervalMotorDays = intervalMotor?.durationDays,
    intervalMotorLabel = intervalMotor?.displayLabel,
    intervalMobilKm = intervalMobil?.distance?.kilometers,
    intervalMobilDays = intervalMobil?.durationDays,
    intervalMobilLabel = intervalMobil?.displayLabel,
    why = why,
    applicableSubtypes = applicableSubtypes,
    tag = tag.key,
    isCustom = isCustom,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun TrackedComponentEntity.toDomain(): TrackedComponent = TrackedComponent(
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
    urgency = urgency.toComponentUrgency(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toDomain(),
)

fun TrackedComponent.toEntity(): TrackedComponentEntity = TrackedComponentEntity(
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
    urgency = urgency.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toEmbed(),
)

private fun buildInterval(km: Long?, days: Int?, label: String?): ComponentInterval? {
    if (km == null && days == null && label.isNullOrBlank()) return null
    return ComponentInterval(
        distance = Distance.ofKmOrNull(km),
        durationDays = days,
        displayLabel = label.orEmpty(),
    )
}

private fun String.toComponentUrgency(): ComponentUrgency =
    runCatching { ComponentUrgency.valueOf(this) }.getOrDefault(ComponentUrgency.Ok)
