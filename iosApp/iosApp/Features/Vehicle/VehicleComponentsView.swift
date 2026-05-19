import SwiftUI

struct VehicleComponentsView: View {
    var vehicleType: String = "motor"
    var subtype: String = "matic"
    var vehicleName: String = "Beat Hitam"

    @Environment(AppRouter.self) private var router

    private var components: [ComponentInfo] {
        ComponentsCatalog.forSubtype(subtype)
    }

    private var subLabel: String {
        VehicleSubtypes.label(for: vehicleType, id: subtype)
    }

    private var typeLabel: String {
        vehicleType == "mobil" ? "Mobil" : "Motor"
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                SubtypeBanner(subLabel: subLabel, typeLabel: typeLabel, onChange: {})
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 12)

                ComponentsList(
                    components: components,
                    vehicleType: vehicleType,
                    onOpenComponent: { id in
                        router.navigate(to: .componentDetail(componentId: id))
                    }
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 12)

                AddComponentRow(onAdd: { router.navigate(to: .addCustomComponent) })
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Komponen")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack(spacing: 1) {
                    Text("Komponen")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 16))
                        .foregroundColor(.sgTextPrimary)
                    Text("\(vehicleName) · \(components.count) dipantau")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: { router.navigate(to: .addCustomComponent) }) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 10)
                            .fill(Color.sgPrimary)
                            .frame(width: 32, height: 32)
                        Text("\u{2b}")
                            .font(.custom("FontAwesome6Free-Solid", size: 14))
                            .foregroundColor(.white)
                    }
                }
            }
        }
    }
}

private struct SubtypeBanner: View {
    let subLabel: String
    let typeLabel: String
    let onChange: () -> Void

    private let accent = Color(red: 0.18, green: 0.55, blue: 0.34)

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(accent.opacity(0.13))
                    .frame(width: 44, height: 44)
                Text("\u{f21c}")
                    .font(.custom("FontAwesome6Free-Solid", size: 22))
                    .foregroundColor(accent)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text("SUB-TIPE")
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .kerning(0.5)
                    .foregroundColor(.sgTextMuted)
                Text("\(subLabel) · \(typeLabel)")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 15))
                    .foregroundColor(.sgTextPrimary)
            }
            Spacer()
            Button(action: onChange) {
                Text("Ubah")
                    .font(.custom("PlusJakartaSans-Bold", size: 12))
                    .foregroundColor(.sgTextPrimary)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(Color.sgSurface)
                    .clipShape(Capsule())
                    .overlay(Capsule().strokeBorder(Color.sgBorder, lineWidth: 1))
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(
            LinearGradient(
                colors: [accent.opacity(0.13), accent.opacity(0.03)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(accent.opacity(0.2), lineWidth: 1)
        )
    }
}

private struct ComponentsList: View {
    let components: [ComponentInfo]
    let vehicleType: String
    let onOpenComponent: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(components.enumerated()), id: \.element.id) { index, c in
                if index > 0 {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
                Button(action: { onOpenComponent(c.id) }) {
                    ComponentRow(
                        component: c,
                        interval: c.interval(for: vehicleType),
                        last: Self.lastService(for: c.id),
                        urgency: c.id == "oli_mesin" ? .overdue : .ok
                    )
                }
                .buttonStyle(.plain)
            }
        }
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }

    private static func lastService(for id: String) -> String {
        switch id {
        case "oli_mesin":    return "2.420 km lalu · 20 Feb 2026"
        case "kampas_rem":   return "belum tercatat"
        case "ban":          return "4.200 km lalu"
        case "busi":         return "4.600 km lalu"
        case "aki":          return "8 bulan lalu"
        case "vbelt":        return "belum tercatat"
        case "rantai":       return "1.200 km lalu"
        case "filter_udara": return "4.500 km lalu"
        default:             return "belum tercatat"
        }
    }
}

private struct ComponentRow: View {
    let component: ComponentInfo
    let interval: String
    let last: String
    let urgency: ReminderUrgency

    var body: some View {
        HStack(spacing: 12) {
            IconBadge(
                iconUnicode: component.iconUnicode,
                foreground: component.color,
                background: component.color.opacity(0.13),
                size: 38, iconSize: 20, corner: 10
            )
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 6) {
                    Text(component.label)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    if urgency == .overdue {
                        StatusDot(urgency: urgency)
                    }
                }
                Text("Terakhir: \(last)")
                    .font(.custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 1) {
                Text("INTERVAL")
                    .font(.custom("PlusJakartaSans-Bold", size: 10))
                    .kerning(0.5)
                    .foregroundColor(.sgTextSubtle)
                Text(interval)
                    .font(.system(size: 11, weight: .semibold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
            }
            Text("\u{f054}")
                .font(.custom("FontAwesome6Free-Solid", size: 11))
                .foregroundColor(.sgTextSubtle)
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }
}

private struct AddComponentRow: View {
    let onAdd: () -> Void

    var body: some View {
        Button(action: onAdd) {
            HStack(spacing: 12) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.sgSurfaceAlt)
                        .frame(width: 36, height: 36)
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.sgTextPrimary)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text("Tambah komponen")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                    Text("Dari katalog atau ketik sendiri")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .dashedBorder(cornerRadius: 16, lineWidth: 1.5, color: Color.sgBorder.opacity(1.4))
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    NavigationStack { VehicleComponentsView() }
        .environment(AppRouter())
}
