package com.zrifapps.goservice.ui.vehicle.components

import androidx.compose.ui.graphics.Color
import com.zrifapps.goservice.ui.theme.FaIcons

enum class ComponentTag { Core, Plus, Pro }

data class VehicleSubtype(
    val id: String,
    val label: String,
    val desc: String,
    val sample: String,
)

data class ComponentInfo(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color,
    val intervalMotor: String? = null,
    val intervalMobil: String? = null,
    val why: String,
    val subtypes: List<String>,
    val tag: ComponentTag,
) {
    fun intervalFor(vehicleType: String): String {
        if (vehicleType == "mobil") return intervalMobil ?: intervalMotor ?: "—"
        return intervalMotor ?: intervalMobil ?: "—"
    }
}

private const val ALL = "*"

object VehicleSubtypes {
    val motor = listOf(
        VehicleSubtype("matic",   "Matic",          "Transmisi otomatis CVT",       "BeAT, Vario, NMAX, PCX"),
        VehicleSubtype("kopling", "Sport / Kopling","Pakai kopling manual",         "CB150R, R15, Vixion, Ninja"),
        VehicleSubtype("bebek",   "Bebek / Cub",    "Semi-otomatis, persneling kaki","Revo, Supra X, Jupiter Z"),
        VehicleSubtype("moge",    "Moge",           "Mesin > 250cc",                 "CBR, Ninja 650, MT-07"),
    )
    val mobil = listOf(
        VehicleSubtype("mpv",       "MPV",        "7-seater keluarga",              "Avanza, Xenia, Innova, Xpander"),
        VehicleSubtype("suv",       "SUV",        "Ground clearance tinggi",        "Fortuner, CR-V, Pajero, Terios"),
        VehicleSubtype("sedan",     "Sedan",      "4-pintu, bagasi terpisah",       "Civic, Camry, City, Accord"),
        VehicleSubtype("hatchback", "Hatchback",  "Compact, pintu belakang menyatu","Brio, Yaris, Jazz, Agya"),
        VehicleSubtype("pickup",    "Pick-up",    "Bak terbuka untuk barang",       "Hilux, D-Max, Triton, Strada"),
    )

    fun forType(vehicleType: String): List<VehicleSubtype> =
        if (vehicleType == "mobil") mobil else motor

    fun labelOf(vehicleType: String, id: String): String =
        forType(vehicleType).firstOrNull { it.id == id }?.label ?: id

    fun defaultFor(vehicleType: String): String =
        if (vehicleType == "mobil") "mpv" else "matic"
}

object ComponentsCatalog {
    val all: List<ComponentInfo> = listOf(
        // Universal
        ComponentInfo("oli_mesin",   "Oli Mesin",         FaIcons.OIL_CAN,         Color(0xFFE89C2E),
            intervalMotor = "2.000 km / 2 bln", intervalMobil = "5.000 km / 6 bln",
            why = "Pelumas utama mesin — tidak ganti tepat waktu = mesin cepat aus.",
            subtypes = listOf(ALL), tag = ComponentTag.Core),
        ComponentInfo("busi",        "Busi",              FaIcons.BOLT,            Color(0xFFE8B62E),
            intervalMotor = "6.000 km", intervalMobil = "20.000 km",
            why = "Pemantik pembakaran — busi mati = motor susah hidup.",
            subtypes = listOf(ALL), tag = ComponentTag.Core),
        ComponentInfo("aki",         "Aki",               FaIcons.CAR_BATTERY,     Color(0xFFD6453A),
            intervalMotor = "1–2 tahun", intervalMobil = "2–3 tahun",
            why = "Sumber listrik untuk starter & lampu. Habis = mogok.",
            subtypes = listOf(ALL), tag = ComponentTag.Core),
        ComponentInfo("kampas_rem",  "Kampas Rem",        FaIcons.CIRCLE_NOTCH,    Color(0xFF2E8B57),
            intervalMotor = "8.000 km", intervalMobil = "20.000 km",
            why = "Komponen rem — tipis = rem tidak pakem & bahaya.",
            subtypes = listOf(ALL), tag = ComponentTag.Core),
        ComponentInfo("ban",         "Ban",               FaIcons.LIFE_RING,       Color(0xFF3F4D5C),
            intervalMotor = "15.000 km", intervalMobil = "40.000 km",
            why = "Cek kembang & tekanan. Gundul atau pecah = berbahaya.",
            subtypes = listOf(ALL), tag = ComponentTag.Core),
        ComponentInfo("filter_udara","Filter Udara",      FaIcons.FILTER,          Color(0xFF7B6FE8),
            intervalMotor = "8.000 km", intervalMobil = "15.000 km",
            why = "Saring debu masuk mesin. Kotor = boros bensin, tenaga turun.",
            subtypes = listOf(ALL), tag = ComponentTag.Plus),

        // Motor matic
        ComponentInfo("vbelt",       "V-Belt CVT",        FaIcons.GEAR,           Color(0xFFA85B2E),
            intervalMotor = "24.000 km",
            why = "Penggerak roda di motor matic. Putus = motor mogok di jalan.",
            subtypes = listOf("matic"), tag = ComponentTag.Core),
        ComponentInfo("roller",      "Roller CVT",        FaIcons.GEAR,           Color(0xFF888888),
            intervalMotor = "24.000 km",
            why = "Bantu CVT bekerja halus. Aus = motor terasa kasar saat tarik gas.",
            subtypes = listOf("matic"), tag = ComponentTag.Plus),
        ComponentInfo("oli_gardan",  "Oli Gardan",        FaIcons.OIL_CAN,         Color(0xFFE89C2E),
            intervalMotor = "8.000 km",
            why = "Pelumas gigi belakang khusus matic. Kering = bunyi & rusak.",
            subtypes = listOf("matic"), tag = ComponentTag.Plus),

        // Motor kopling / moge
        ComponentInfo("kampas_kopling","Kampas Kopling",  FaIcons.GEAR,           Color(0xFFA85B2E),
            intervalMotor = "15.000 km",
            why = "Penghubung tenaga mesin ke roda. Slip = motor tidak nyaman.",
            subtypes = listOf("kopling", "moge"), tag = ComponentTag.Plus),
        ComponentInfo("oli_kopling", "Oli Transmisi",     FaIcons.OIL_CAN,         Color(0xFFE89C2E),
            intervalMotor = "10.000 km",
            why = "Lumasi gearbox motor kopling.",
            subtypes = listOf("kopling", "moge"), tag = ComponentTag.Plus),

        // Motor kopling / bebek / moge — rantai
        ComponentInfo("rantai",      "Rantai & Gear",     FaIcons.GEAR,           Color(0xFF5C6357),
            intervalMotor = "8.000 km",
            why = "Penggerak roda belakang. Kendor/aus = bunyi & putus.",
            subtypes = listOf("kopling", "bebek", "moge"), tag = ComponentTag.Core),

        // Mobil
        ComponentInfo("filter_oli",  "Filter Oli",        FaIcons.FILTER,          Color(0xFF7B6FE8),
            intervalMobil = "10.000 km",
            why = "Saring kotoran oli. Biasanya ganti bareng oli mesin.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup"), tag = ComponentTag.Core),
        ComponentInfo("filter_ac",   "Filter Kabin / AC", FaIcons.FILTER,          Color(0xFF3FB1D6),
            intervalMobil = "15.000 km",
            why = "Saring udara masuk kabin. Bau apek? Saatnya ganti.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup"), tag = ComponentTag.Plus),
        ComponentInfo("radiator",    "Cairan Radiator",   FaIcons.TEMPERATURE_HALF, Color(0xFF3FB1D6),
            intervalMotor = "tahunan", intervalMobil = "40.000 km / 2 thn",
            why = "Pendingin mesin. Kosong = mesin overheat.",
            subtypes = listOf("suv", "mpv", "sedan", "hatchback", "pickup", "moge"), tag = ComponentTag.Plus),
        ComponentInfo("minyak_rem",  "Minyak Rem",        FaIcons.CIRCLE_NOTCH,    Color(0xFFD6453A),
            intervalMobil = "40.000 km / 2 thn",
            why = "Tekanan hidrolik untuk rem. Kurang = pedal amblas.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup"), tag = ComponentTag.Plus),
        ComponentInfo("wiper",       "Wiper",             FaIcons.WRENCH,          Color(0xFF3FB1D6),
            intervalMobil = "6–12 bulan",
            why = "Karet wiper aus = kaca tidak bersih saat hujan.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup"), tag = ComponentTag.Pro),
        ComponentInfo("timing_belt", "Timing Belt",       FaIcons.GEAR,           Color(0xFFA85B2E),
            intervalMobil = "60.000 km",
            why = "Sinkronkan klep & piston. Putus = mesin rusak parah.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup"), tag = ComponentTag.Plus),
        ComponentInfo("shock",       "Shock Breaker",     FaIcons.GEAR,           Color(0xFF5C6357),
            intervalMobil = "40.000 km",
            why = "Peredam guncangan. Bocor = mobil limbung.",
            subtypes = listOf("mpv", "suv", "sedan", "hatchback", "pickup", "moge"), tag = ComponentTag.Pro),
        ComponentInfo("tune_up",     "Tune Up Berkala",   FaIcons.WRENCH,          Color(0xFF5C6357),
            intervalMotor = "5.000 km", intervalMobil = "10.000 km",
            why = "Pengecekan & penyetelan umum di bengkel resmi.",
            subtypes = listOf(ALL), tag = ComponentTag.Plus),
    )

    fun forSubtype(subtype: String): List<ComponentInfo> =
        all.filter { it.subtypes.contains(ALL) || it.subtypes.contains(subtype) }

    fun byId(id: String): ComponentInfo? = all.firstOrNull { it.id == id }
}

fun ComponentTag.label(): String = when (this) {
    ComponentTag.Core -> "Wajib"
    ComponentTag.Plus -> "Umum"
    ComponentTag.Pro  -> "Opsional"
}
