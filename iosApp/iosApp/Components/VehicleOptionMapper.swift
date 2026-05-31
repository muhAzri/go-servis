import SwiftUI
import Shared

extension Vehicle {
    func toVehicleOption() -> VehicleOption {
        let icon = type.key == "motor" ? "\u{f21c}" : "\u{f1b9}"
        let hex = PresentationFactory.shared.vehicleColorHex(vehicle: self)
        let accent: Color = Color(hex: hex) ?? .sgPrimary
        return VehicleOption(
            id: id,
            name: displayTitle,
            plate: plateNumber,
            iconUnicode: icon,
            accent: accent
        )
    }
}

private extension Color {
    init?(hex: String) {
        var s = hex
        if s.hasPrefix("#") { s.removeFirst() }
        guard s.count == 6 || s.count == 8, let v = UInt64(s, radix: 16) else { return nil }
        let r, g, b, a: Double
        if s.count == 6 {
            r = Double((v >> 16) & 0xFF) / 255.0
            g = Double((v >> 8) & 0xFF) / 255.0
            b = Double(v & 0xFF) / 255.0
            a = 1.0
        } else {
            r = Double((v >> 24) & 0xFF) / 255.0
            g = Double((v >> 16) & 0xFF) / 255.0
            b = Double((v >> 8) & 0xFF) / 255.0
            a = Double(v & 0xFF) / 255.0
        }
        self.init(.sRGB, red: r, green: g, blue: b, opacity: a)
    }
}
