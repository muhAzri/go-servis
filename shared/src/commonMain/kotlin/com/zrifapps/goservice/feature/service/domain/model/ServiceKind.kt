package com.zrifapps.goservice.feature.service.domain.model

enum class ServiceKind(val key: String) {
    /** Pemilihan komponen spesifik dari katalog tracked vehicle. Auto-reminder per komponen. */
    Komponen("komponen"),

    /** Servis rutin/berkala. Tidak terikat komponen. Auto-reminder berdasarkan interval rutin kendaraan. */
    Rutin("rutin"),

    /** Servis satu kali dengan judul bebas (mis. ganti spion). Tidak generate reminder. */
    Manual("manual");

    companion object {
        fun fromKey(key: String?): ServiceKind = entries.firstOrNull { it.key == key } ?: Komponen
    }
}
