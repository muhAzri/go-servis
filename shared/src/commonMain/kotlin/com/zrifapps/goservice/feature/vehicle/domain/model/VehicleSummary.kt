package com.zrifapps.goservice.feature.vehicle.domain.model

import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money

data class VehicleSummary(
    val vehicle: Vehicle,
    val totalServiceCount: Int,
    val totalServiceCost: Money,
    val lastServicedAt: Long?,
    val lastServicedOdometer: Distance?,
    val activeReminderCount: Int,
    val overdueReminderCount: Int,
)
