package com.zrifapps.goservice.feature.service.domain.model

enum class ServiceType(val key: String) {
    OilChange("oli"),
    Filter("filter"),
    Tire("ban"),
    Battery("aki"),
    Brake("rem"),
    Radiator("radiator"),
    TuneUp("tune_up"),
    Other("other");

    companion object {
        fun fromKey(key: String): ServiceType = entries.firstOrNull { it.key == key } ?: Other
    }
}
