package com.zrifapps.goservice.feature.component.data.seed

import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentInterval
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag

private const val ALL = "*"
private const val DAYS_PER_MONTH = 30
private const val DAYS_PER_YEAR = 365

// Motor subtypes
private const val MATIC = "matic"
private const val KOPLING = "kopling"
private const val BEBEK = "bebek"
private const val MOGE = "moge"

// Mobil subtypes
private const val MPV = "mpv"
private const val SUV = "suv"
private const val SEDAN = "sedan"
private const val HATCHBACK = "hatchback"
private const val PICKUP = "pickup"

private val ALL_MOBIL = listOf(MPV, SUV, SEDAN, HATCHBACK, PICKUP)
private val MOTOR_LIQUID_COOLED = listOf(MATIC, KOPLING, MOGE)
private val MOTOR_CHAIN_DRIVE = listOf(KOPLING, BEBEK, MOGE)

object DefaultComponentCatalog {

    fun all(now: Long): List<Component> = listOf(
        // ---------------------------------------------------------------------
        // Universal (motor + mobil)
        // ---------------------------------------------------------------------
        component(
            id = "oli_mesin",
            label = "Oli Mesin",
            iconKey = "OIL_CAN",
            colorHex = "#E89C2E",
            intervalMotor = ComponentInterval(Distance.ofKm(2_000), 2 * DAYS_PER_MONTH, "2.000 km / 2 bln"),
            intervalMobil = ComponentInterval(Distance.ofKm(10_000), 6 * DAYS_PER_MONTH, "10.000 km / 6 bln"),
            why = "Pelumas utama mesin — telat ganti = mesin cepat aus & boros bensin.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "busi",
            label = "Busi",
            iconKey = "BOLT",
            colorHex = "#E8B62E",
            intervalMotor = ComponentInterval(Distance.ofKm(8_000), null, "8.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(20_000), null, "20.000 km"),
            why = "Pemantik pembakaran — busi lemah = mesin susah hidup & tarikan berat.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "aki",
            label = "Aki",
            iconKey = "CAR_BATTERY",
            colorHex = "#D6453A",
            intervalMotor = ComponentInterval(null, DAYS_PER_YEAR, "1–2 tahun"),
            intervalMobil = ComponentInterval(null, 2 * DAYS_PER_YEAR, "2–3 tahun"),
            why = "Sumber listrik untuk starter & kelistrikan. Soak = mogok.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "kampas_rem",
            label = "Kampas Rem",
            iconKey = "CIRCLE_NOTCH",
            colorHex = "#2E8B57",
            intervalMotor = ComponentInterval(Distance.ofKm(10_000), null, "10.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(30_000), null, "30.000 km"),
            why = "Penentu daya pengereman — tipis = rem blong & bahaya.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "ban",
            label = "Ban",
            iconKey = "LIFE_RING",
            colorHex = "#3F4D5C",
            intervalMotor = ComponentInterval(Distance.ofKm(15_000), 3 * DAYS_PER_YEAR, "15.000 km / 3 thn"),
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), 5 * DAYS_PER_YEAR, "40.000 km / 5 thn"),
            why = "Satu-satunya kontak ke jalan. Gundul/getas = licin & rawan pecah.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "filter_udara",
            label = "Filter Udara",
            iconKey = "FILTER",
            colorHex = "#7B6FE8",
            intervalMotor = ComponentInterval(Distance.ofKm(16_000), null, "16.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            why = "Saring udara ke ruang bakar. Kotor = boros & tenaga turun.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "tune_up",
            label = "Tune Up Berkala",
            iconKey = "WRENCH",
            colorHex = "#5C6357",
            intervalMotor = ComponentInterval(Distance.ofKm(4_000), null, "4.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(10_000), 6 * DAYS_PER_MONTH, "10.000 km / 6 bln"),
            why = "Pengecekan & penyetelan umum di bengkel.",
            applicableSubtypes = listOf(ALL),
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "radiator",
            label = "Cairan Radiator (Coolant)",
            iconKey = "TEMPERATURE_HALF",
            colorHex = "#3FB1D6",
            intervalMotor = ComponentInterval(Distance.ofKm(12_000), DAYS_PER_YEAR, "12.000 km / tahunan"),
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), 2 * DAYS_PER_YEAR, "40.000 km / 2 thn"),
            why = "Pendingin mesin. Kurang/kotor = mesin overheat. Hanya mesin berpendingin cairan.",
            applicableSubtypes = MOTOR_LIQUID_COOLED + ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "minyak_rem",
            label = "Minyak Rem",
            iconKey = "CIRCLE_NOTCH",
            colorHex = "#D6453A",
            intervalMotor = ComponentInterval(null, 2 * DAYS_PER_YEAR, "2 tahun"),
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), 2 * DAYS_PER_YEAR, "40.000 km / 2 thn"),
            why = "Tekanan hidrolik untuk rem cakram. Kurang/menyerap air = pedal/tuas amblas.",
            applicableSubtypes = listOf(KOPLING, MOGE) + ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "filter_oli",
            label = "Filter Oli",
            iconKey = "FILTER",
            colorHex = "#7B6FE8",
            intervalMotor = ComponentInterval(Distance.ofKm(6_000), null, "6.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(10_000), null, "10.000 km"),
            why = "Saring kotoran oli. Umumnya diganti bareng oli mesin.",
            applicableSubtypes = listOf(KOPLING, MOGE) + ALL_MOBIL,
            tag = ComponentTag.Core,
            now = now,
        ),

        // ---------------------------------------------------------------------
        // Motor — Matic (CVT)
        // ---------------------------------------------------------------------
        component(
            id = "oli_gardan",
            label = "Oli Gardan",
            iconKey = "OIL_CAN",
            colorHex = "#C98A2E",
            intervalMotor = ComponentInterval(Distance.ofKm(8_000), null, "8.000 km"),
            intervalMobil = null,
            why = "Pelumas gigi reduksi matic. Kering = bunyi kasar & gardan rusak. Sering terlupa!",
            applicableSubtypes = listOf(MATIC),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "vbelt",
            label = "V-Belt CVT",
            iconKey = "GEAR",
            colorHex = "#A85B2E",
            intervalMotor = ComponentInterval(Distance.ofKm(24_000), null, "24.000 km"),
            intervalMobil = null,
            why = "Penggerak roda di matic. Putus = motor mogok mendadak di jalan.",
            applicableSubtypes = listOf(MATIC),
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "roller",
            label = "Roller CVT",
            iconKey = "GEARS",
            colorHex = "#888888",
            intervalMotor = ComponentInterval(Distance.ofKm(24_000), null, "24.000 km"),
            intervalMobil = null,
            why = "Bantu CVT bekerja halus. Aus = akselerasi kasar & bergetar.",
            applicableSubtypes = listOf(MATIC),
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "kampas_ganda",
            label = "Kampas Ganda",
            iconKey = "GEAR",
            colorHex = "#A85B2E",
            intervalMotor = ComponentInterval(Distance.ofKm(24_000), null, "24.000 km"),
            intervalMobil = null,
            why = "Kopling sentrifugal matic. Aus = selip & tarikan awal lemah.",
            applicableSubtypes = listOf(MATIC),
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "filter_cvt",
            label = "Filter Udara CVT",
            iconKey = "FILTER",
            colorHex = "#7B6FE8",
            intervalMotor = ComponentInterval(Distance.ofKm(16_000), null, "16.000 km"),
            intervalMobil = null,
            why = "Saring udara pendingin ruang CVT. Kotor = CVT cepat panas.",
            applicableSubtypes = listOf(MATIC),
            tag = ComponentTag.Pro,
            now = now,
        ),

        // ---------------------------------------------------------------------
        // Motor — Sport/Kopling, Bebek, Moge (rantai / kopling manual)
        // ---------------------------------------------------------------------
        component(
            id = "rantai",
            label = "Rantai & Gear",
            iconKey = "GEARS",
            colorHex = "#5C6357",
            intervalMotor = ComponentInterval(Distance.ofKm(8_000), null, "8.000 km"),
            intervalMobil = null,
            why = "Penggerak roda belakang. Kendor/aus = bunyi, selip, bisa putus.",
            applicableSubtypes = MOTOR_CHAIN_DRIVE,
            tag = ComponentTag.Core,
            now = now,
        ),
        component(
            id = "kampas_kopling",
            label = "Kampas Kopling",
            iconKey = "GEAR",
            colorHex = "#A85B2E",
            intervalMotor = ComponentInterval(Distance.ofKm(20_000), null, "20.000 km"),
            intervalMobil = null,
            why = "Penyalur tenaga mesin ke transmisi. Aus = selip & tarikan loyo.",
            applicableSubtypes = MOTOR_CHAIN_DRIVE,
            tag = ComponentTag.Plus,
            now = now,
        ),

        // ---------------------------------------------------------------------
        // Mobil
        // ---------------------------------------------------------------------
        component(
            id = "filter_ac",
            label = "Filter Kabin / AC",
            iconKey = "FILTER",
            colorHex = "#3FB1D6",
            intervalMotor = null,
            intervalMobil = ComponentInterval(Distance.ofKm(15_000), DAYS_PER_YEAR, "15.000 km / tahunan"),
            why = "Saring udara masuk kabin. Kotor = AC bau apek & kurang dingin.",
            applicableSubtypes = ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "oli_transmisi",
            label = "Oli Transmisi / ATF",
            iconKey = "OIL_CAN",
            colorHex = "#2E8B57",
            intervalMotor = null,
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            why = "Pelumas transmisi (matic/CVT/manual). Telat = perpindahan gigi kasar & cepat rusak.",
            applicableSubtypes = ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "fan_belt",
            label = "Fan Belt / V-Belt",
            iconKey = "GEAR",
            colorHex = "#A85B2E",
            intervalMotor = null,
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            why = "Penggerak alternator, AC & power steering. Putus = aki tekor & AC mati.",
            applicableSubtypes = ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "timing_belt",
            label = "Timing Belt",
            iconKey = "GEAR",
            colorHex = "#A85B2E",
            intervalMotor = null,
            intervalMobil = ComponentInterval(Distance.ofKm(60_000), null, "60.000–100.000 km"),
            why = "Sinkronkan klep & piston (mesin tipe belt). Putus = mesin rusak parah.",
            applicableSubtypes = ALL_MOBIL,
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "oli_gardan_mobil",
            label = "Oli Gardan / Differential",
            iconKey = "OIL_CAN",
            colorHex = "#E89C2E",
            intervalMotor = null,
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            why = "Pelumas gardan untuk mobil penggerak belakang/4WD. Kering = bunyi dengung & aus.",
            applicableSubtypes = listOf(SUV, PICKUP, MPV),
            tag = ComponentTag.Plus,
            now = now,
        ),
        component(
            id = "wiper",
            label = "Wiper",
            iconKey = "WRENCH",
            colorHex = "#3FB1D6",
            intervalMotor = null,
            intervalMobil = ComponentInterval(null, 6 * DAYS_PER_MONTH, "6–12 bulan"),
            why = "Karet wiper getas = kaca tidak bersih saat hujan & berbahaya.",
            applicableSubtypes = ALL_MOBIL,
            tag = ComponentTag.Pro,
            now = now,
        ),
        component(
            id = "shock",
            label = "Shock Breaker",
            iconKey = "GEAR",
            colorHex = "#5C6357",
            intervalMotor = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            intervalMobil = ComponentInterval(Distance.ofKm(40_000), null, "40.000 km"),
            why = "Peredam guncangan. Bocor/lemah = bodi limbung & ban cepat aus.",
            applicableSubtypes = listOf(MOGE) + ALL_MOBIL,
            tag = ComponentTag.Pro,
            now = now,
        ),
    )

    private fun component(
        id: String,
        label: String,
        iconKey: String,
        colorHex: String,
        intervalMotor: ComponentInterval?,
        intervalMobil: ComponentInterval?,
        why: String,
        applicableSubtypes: List<String>,
        tag: ComponentTag,
        now: Long,
    ): Component = Component(
        id = id,
        label = label,
        iconKey = iconKey,
        colorHex = colorHex,
        intervalMotor = intervalMotor,
        intervalMobil = intervalMobil,
        why = why,
        applicableSubtypes = applicableSubtypes,
        tag = tag,
        isCustom = false,
        createdAt = now,
        updatedAt = now,
    )
}
