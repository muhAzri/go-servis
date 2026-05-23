package com.zrifapps.goservice.feature.vehicle.domain.model

enum class VehicleType(val key: String) {
    Motor("motor"),
    Mobil("mobil");

    companion object {
        fun fromKey(key: String): VehicleType = entries.firstOrNull { it.key == key } ?: Motor
    }
}
