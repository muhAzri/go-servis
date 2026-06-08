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

        /** Turunkan jenis servis representatif dari komponen yang diservis. */
        fun fromComponentIds(componentIds: Collection<String>): ServiceType {
            val first = componentIds.firstOrNull() ?: return Other
            return when (first) {
                "oli_mesin", "oli_gardan", "oli_gardan_mobil", "oli_transmisi" -> OilChange
                "filter_oli", "filter_udara", "filter_ac", "filter_cvt" -> Filter
                "ban" -> Tire
                "aki" -> Battery
                "kampas_rem", "minyak_rem" -> Brake
                "radiator" -> Radiator
                "busi", "tune_up" -> TuneUp
                else -> Other
            }
        }
    }
}
