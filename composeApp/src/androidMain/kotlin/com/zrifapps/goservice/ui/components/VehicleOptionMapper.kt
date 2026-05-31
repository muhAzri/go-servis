package com.zrifapps.goservice.ui.components

import androidx.compose.ui.graphics.Color
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons

fun Vehicle.toVehicleOption(): VehicleOption {
    val icon = if (type == VehicleType.Motor) FaIcons.MOTORCYCLE else FaIcons.CAR
    val accent = runCatching { Color(android.graphics.Color.parseColor(color.value)) }
        .getOrDefault(AppColors.Primary)
    return VehicleOption(
        id = id,
        name = displayTitle,
        plate = plateNumber,
        icon = icon,
        accent = accent,
    )
}
