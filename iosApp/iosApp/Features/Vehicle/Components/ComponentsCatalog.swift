import SwiftUI

enum ComponentTag {
    case core, plus, pro

    var label: String {
        switch self {
        case .core: return "Wajib"
        case .plus: return "Umum"
        case .pro:  return "Opsional"
        }
    }
}

struct VehicleSubtype: Identifiable, Hashable {
    let id: String
    let label: String
    let desc: String
    let sample: String
}

struct ComponentInfo: Identifiable, Hashable {
    let id: String
    let label: String
    let iconUnicode: String
    let color: Color
    let intervalMotor: String?
    let intervalMobil: String?
    let why: String
    let subtypes: [String]   // ["*"] for universal
    let tag: ComponentTag

    func interval(for vehicleType: String) -> String {
        if vehicleType == "mobil" {
            return intervalMobil ?? intervalMotor ?? "—"
        }
        return intervalMotor ?? intervalMobil ?? "—"
    }

    func matches(subtype: String) -> Bool {
        subtypes.contains("*") || subtypes.contains(subtype)
    }
}

enum VehicleSubtypes {
    static let motor: [VehicleSubtype] = [
        .init(id: "matic",   label: "Matic",          desc: "Transmisi otomatis CVT",        sample: "BeAT, Vario, NMAX, PCX"),
        .init(id: "kopling", label: "Sport / Kopling",desc: "Pakai kopling manual",          sample: "CB150R, R15, Vixion, Ninja"),
        .init(id: "bebek",   label: "Bebek / Cub",    desc: "Semi-otomatis, persneling kaki",sample: "Revo, Supra X, Jupiter Z"),
        .init(id: "moge",    label: "Moge",           desc: "Mesin > 250cc",                 sample: "CBR, Ninja 650, MT-07"),
    ]
    static let mobil: [VehicleSubtype] = [
        .init(id: "mpv",       label: "MPV",       desc: "7-seater keluarga",               sample: "Avanza, Xenia, Innova, Xpander"),
        .init(id: "suv",       label: "SUV",       desc: "Ground clearance tinggi",         sample: "Fortuner, CR-V, Pajero, Terios"),
        .init(id: "sedan",     label: "Sedan",     desc: "4-pintu, bagasi terpisah",        sample: "Civic, Camry, City, Accord"),
        .init(id: "hatchback", label: "Hatchback", desc: "Compact, pintu belakang menyatu", sample: "Brio, Yaris, Jazz, Agya"),
        .init(id: "pickup",    label: "Pick-up",   desc: "Bak terbuka untuk barang",        sample: "Hilux, D-Max, Triton, Strada"),
    ]

    static func list(for vehicleType: String) -> [VehicleSubtype] {
        vehicleType == "mobil" ? mobil : motor
    }

    static func label(for vehicleType: String, id: String) -> String {
        list(for: vehicleType).first(where: { $0.id == id })?.label ?? id
    }

    static func defaultId(for vehicleType: String) -> String {
        vehicleType == "mobil" ? "mpv" : "matic"
    }
}

enum ComponentsCatalog {
    static let all: [ComponentInfo] = [
        // Universal
        .init(id: "oli_mesin",    label: "Oli Mesin",          iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18),
              intervalMotor: "2.000 km / 2 bln", intervalMobil: "5.000 km / 6 bln",
              why: "Pelumas utama mesin — tidak ganti tepat waktu = mesin cepat aus.",
              subtypes: ["*"], tag: .core),
        .init(id: "busi",         label: "Busi",                iconUnicode: "\u{f0e7}", color: Color(red: 0.91, green: 0.71, blue: 0.18),
              intervalMotor: "6.000 km", intervalMobil: "20.000 km",
              why: "Pemantik pembakaran — busi mati = motor susah hidup.",
              subtypes: ["*"], tag: .core),
        .init(id: "aki",          label: "Aki",                 iconUnicode: "\u{f5df}", color: Color(red: 0.84, green: 0.27, blue: 0.23),
              intervalMotor: "1–2 tahun", intervalMobil: "2–3 tahun",
              why: "Sumber listrik untuk starter & lampu. Habis = mogok.",
              subtypes: ["*"], tag: .core),
        .init(id: "kampas_rem",   label: "Kampas Rem",          iconUnicode: "\u{f1ce}", color: Color(red: 0.18, green: 0.55, blue: 0.34),
              intervalMotor: "8.000 km", intervalMobil: "20.000 km",
              why: "Komponen rem — tipis = rem tidak pakem & bahaya.",
              subtypes: ["*"], tag: .core),
        .init(id: "ban",          label: "Ban",                 iconUnicode: "\u{f1cd}", color: Color(red: 0.25, green: 0.30, blue: 0.36),
              intervalMotor: "15.000 km", intervalMobil: "40.000 km",
              why: "Cek kembang & tekanan. Gundul atau pecah = berbahaya.",
              subtypes: ["*"], tag: .core),
        .init(id: "filter_udara", label: "Filter Udara",        iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91),
              intervalMotor: "8.000 km", intervalMobil: "15.000 km",
              why: "Saring debu masuk mesin. Kotor = boros bensin, tenaga turun.",
              subtypes: ["*"], tag: .plus),

        // Motor matic
        .init(id: "vbelt",        label: "V-Belt CVT",          iconUnicode: "\u{f013}", color: Color(red: 0.66, green: 0.36, blue: 0.18),
              intervalMotor: "24.000 km", intervalMobil: nil,
              why: "Penggerak roda di motor matic. Putus = motor mogok di jalan.",
              subtypes: ["matic"], tag: .core),
        .init(id: "roller",       label: "Roller CVT",          iconUnicode: "\u{f013}", color: Color(red: 0.53, green: 0.53, blue: 0.53),
              intervalMotor: "24.000 km", intervalMobil: nil,
              why: "Bantu CVT bekerja halus. Aus = motor terasa kasar saat tarik gas.",
              subtypes: ["matic"], tag: .plus),
        .init(id: "oli_gardan",   label: "Oli Gardan",          iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18),
              intervalMotor: "8.000 km", intervalMobil: nil,
              why: "Pelumas gigi belakang khusus matic. Kering = bunyi & rusak.",
              subtypes: ["matic"], tag: .plus),

        // Motor kopling / moge
        .init(id: "kampas_kopling", label: "Kampas Kopling",    iconUnicode: "\u{f013}", color: Color(red: 0.66, green: 0.36, blue: 0.18),
              intervalMotor: "15.000 km", intervalMobil: nil,
              why: "Penghubung tenaga mesin ke roda. Slip = motor tidak nyaman.",
              subtypes: ["kopling", "moge"], tag: .plus),
        .init(id: "oli_kopling",  label: "Oli Transmisi",       iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18),
              intervalMotor: "10.000 km", intervalMobil: nil,
              why: "Lumasi gearbox motor kopling.",
              subtypes: ["kopling", "moge"], tag: .plus),

        // Motor kopling / bebek / moge — rantai
        .init(id: "rantai",       label: "Rantai & Gear",       iconUnicode: "\u{f013}", color: Color(red: 0.36, green: 0.39, blue: 0.34),
              intervalMotor: "8.000 km", intervalMobil: nil,
              why: "Penggerak roda belakang. Kendor/aus = bunyi & putus.",
              subtypes: ["kopling", "bebek", "moge"], tag: .core),

        // Mobil
        .init(id: "filter_oli",   label: "Filter Oli",          iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91),
              intervalMotor: nil, intervalMobil: "10.000 km",
              why: "Saring kotoran oli. Biasanya ganti bareng oli mesin.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup"], tag: .core),
        .init(id: "filter_ac",    label: "Filter Kabin / AC",   iconUnicode: "\u{f0b0}", color: Color(red: 0.25, green: 0.69, blue: 0.84),
              intervalMotor: nil, intervalMobil: "15.000 km",
              why: "Saring udara masuk kabin. Bau apek? Saatnya ganti.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup"], tag: .plus),
        .init(id: "radiator",     label: "Cairan Radiator",     iconUnicode: "\u{f2c9}", color: Color(red: 0.25, green: 0.69, blue: 0.84),
              intervalMotor: "tahunan", intervalMobil: "40.000 km / 2 thn",
              why: "Pendingin mesin. Kosong = mesin overheat.",
              subtypes: ["suv", "mpv", "sedan", "hatchback", "pickup", "moge"], tag: .plus),
        .init(id: "minyak_rem",   label: "Minyak Rem",          iconUnicode: "\u{f1ce}", color: Color(red: 0.84, green: 0.27, blue: 0.23),
              intervalMotor: nil, intervalMobil: "40.000 km / 2 thn",
              why: "Tekanan hidrolik untuk rem. Kurang = pedal amblas.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup"], tag: .plus),
        .init(id: "wiper",        label: "Wiper",               iconUnicode: "\u{f0ad}", color: Color(red: 0.25, green: 0.69, blue: 0.84),
              intervalMotor: nil, intervalMobil: "6–12 bulan",
              why: "Karet wiper aus = kaca tidak bersih saat hujan.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup"], tag: .pro),
        .init(id: "timing_belt",  label: "Timing Belt",         iconUnicode: "\u{f013}", color: Color(red: 0.66, green: 0.36, blue: 0.18),
              intervalMotor: nil, intervalMobil: "60.000 km",
              why: "Sinkronkan klep & piston. Putus = mesin rusak parah.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup"], tag: .plus),
        .init(id: "shock",        label: "Shock Breaker",       iconUnicode: "\u{f013}", color: Color(red: 0.36, green: 0.39, blue: 0.34),
              intervalMotor: nil, intervalMobil: "40.000 km",
              why: "Peredam guncangan. Bocor = mobil limbung.",
              subtypes: ["mpv", "suv", "sedan", "hatchback", "pickup", "moge"], tag: .pro),
        .init(id: "tune_up",      label: "Tune Up Berkala",     iconUnicode: "\u{f0ad}", color: Color(red: 0.36, green: 0.39, blue: 0.34),
              intervalMotor: "5.000 km", intervalMobil: "10.000 km",
              why: "Pengecekan & penyetelan umum di bengkel resmi.",
              subtypes: ["*"], tag: .plus),
    ]

    static func forSubtype(_ subtype: String) -> [ComponentInfo] {
        all.filter { $0.matches(subtype: subtype) }
    }

    static func byId(_ id: String) -> ComponentInfo? {
        all.first(where: { $0.id == id })
    }
}
