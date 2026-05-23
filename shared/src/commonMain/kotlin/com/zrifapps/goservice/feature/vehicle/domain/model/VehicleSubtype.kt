package com.zrifapps.goservice.feature.vehicle.domain.model

data class VehicleSubtype(
    val id: String,
    val parentType: VehicleType,
    val label: String,
    val description: String,
    val sampleModels: String,
)
