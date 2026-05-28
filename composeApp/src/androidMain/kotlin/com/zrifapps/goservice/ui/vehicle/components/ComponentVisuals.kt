package com.zrifapps.goservice.ui.vehicle.components

import androidx.compose.ui.graphics.Color
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentInterval
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag as DomainComponentTag
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.theme.FaIcons

fun Component.faIcon(): String = when (iconKey) {
    "OIL_CAN" -> FaIcons.OIL_CAN
    "BOLT" -> FaIcons.BOLT
    "CAR_BATTERY" -> FaIcons.CAR_BATTERY
    "CIRCLE_NOTCH" -> FaIcons.CIRCLE_NOTCH
    "LIFE_RING" -> FaIcons.LIFE_RING
    "FILTER" -> FaIcons.FILTER
    "TEMPERATURE_HALF" -> FaIcons.TEMPERATURE_HALF
    "GEAR" -> FaIcons.GEAR
    "GEARS" -> FaIcons.GEARS
    "WRENCH" -> FaIcons.WRENCH
    else -> FaIcons.WRENCH
}

fun Component.uiColor(): Color = parseHexColorOrDefault(colorHex)

fun Component.intervalLabelFor(vehicleType: VehicleType): String =
    intervalFor(vehicleType)?.displayLabel ?: "—"

fun DomainComponentTag.label(): String = when (this) {
    DomainComponentTag.Core -> "Wajib"
    DomainComponentTag.Plus -> "Umum"
    DomainComponentTag.Pro -> "Opsional"
}

fun ComponentInterval.summary(): String = displayLabel

private fun parseHexColorOrDefault(hex: String, fallback: Color = Color(0xFF5C6357)): Color =
    try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Throwable) {
        fallback
    }
