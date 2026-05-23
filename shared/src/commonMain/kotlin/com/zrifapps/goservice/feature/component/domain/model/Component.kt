package com.zrifapps.goservice.feature.component.domain.model

import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType

data class Component(
    val id: String,
    val label: String,
    val iconKey: String,
    val colorHex: String,
    val intervalMotor: ComponentInterval?,
    val intervalMobil: ComponentInterval?,
    val why: String,
    val applicableSubtypes: List<String>,
    val tag: ComponentTag,
    val isCustom: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
) {
    fun intervalFor(type: VehicleType): ComponentInterval? = when (type) {
        VehicleType.Mobil -> intervalMobil ?: intervalMotor
        VehicleType.Motor -> intervalMotor ?: intervalMobil
    }

    fun matches(subtypeId: String): Boolean =
        applicableSubtypes.contains(UNIVERSAL_SUBTYPE) || applicableSubtypes.contains(subtypeId)

    companion object {
        const val UNIVERSAL_SUBTYPE: String = "*"
    }
}
