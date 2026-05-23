package com.zrifapps.goservice.feature.vehicle.domain.model

import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor

data class VehicleDraft(
    val nickname: String,
    val type: VehicleType,
    val subtypeId: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val plateNumber: String,
    val odometer: Distance,
    val color: HexColor,
)

enum class VehicleSort {
    InputOrder, Alphabetical, OdometerAsc, OdometerDesc, ReminderUrgency,
}
