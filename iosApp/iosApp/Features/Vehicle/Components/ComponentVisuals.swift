import SwiftUI
import Shared

extension Shared.Component {
    var iconUnicode: String {
        switch iconKey {
        case "OIL_CAN": return "\u{f613}"
        case "BOLT": return "\u{f0e7}"
        case "CAR_BATTERY": return "\u{f5df}"
        case "CIRCLE_NOTCH": return "\u{f1ce}"
        case "LIFE_RING": return "\u{f1cd}"
        case "FILTER": return "\u{f0b0}"
        case "TEMPERATURE_HALF": return "\u{f2c9}"
        case "GEAR": return "\u{f013}"
        case "GEARS": return "\u{e5fe}"
        case "WRENCH": return "\u{f0ad}"
        default: return "\u{f0ad}"
        }
    }

    var uiColor: Color {
        parseHexColor(colorHex) ?? Color(red: 0.36, green: 0.39, blue: 0.34)
    }

    func intervalLabel(for vehicleType: VehicleType) -> String {
        intervalFor(type: vehicleType)?.displayLabel ?? "—"
    }
}

extension Shared.ComponentTag {
    var displayLabel: String {
        switch self {
        case .core: return "Wajib"
        case .plus: return "Umum"
        case .pro: return "Opsional"
        default: return "Umum"
        }
    }
}

private func parseHexColor(_ hex: String) -> Color? {
    let trimmed = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
    var int: UInt64 = 0
    guard Scanner(string: trimmed).scanHexInt64(&int) else { return nil }
    let r = Double((int >> 16) & 0xFF) / 255
    let g = Double((int >> 8) & 0xFF) / 255
    let b = Double(int & 0xFF) / 255
    return Color(red: r, green: g, blue: b)
}
