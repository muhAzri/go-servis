import SwiftUI

struct VehicleDetailView: View {
    var vehicleType: String = "motor"
    var subtype: String = "matic"

    @Environment(AppRouter.self) private var router
    @State private var showShareSheet: Bool = false

    private var components: [ComponentInfo] {
        ComponentsCatalog.forSubtype(subtype)
    }

    private var tilePreview: [ComponentInfo] {
        Array(components.prefix(6))
    }

    private var subLabel: String {
        VehicleSubtypes.label(for: vehicleType, id: subtype)
    }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    VehicleHeroCard()
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                        .padding(.bottom, 16)

                    ComponentsSectionHeader(
                        total: components.count,
                        subLabel: subLabel,
                        onManage: { router.navigate(to: .vehicleComponents) }
                    )

                    ComponentsGrid(
                        components: tilePreview,
                        vehicleType: vehicleType,
                        onOpenComponent: { id in
                            router.navigate(to: .componentDetail(componentId: id))
                        },
                        onAddComponent: { router.navigate(to: .addCustomComponent) }
                    )
                    .padding(.bottom, 16)

                    AdBannerSlot()
                        .padding(.bottom, 16)

                    DetailSectionLabel(text: "Servis terakhir")
                    LastServicesList()
                        .padding(.bottom, 24)
                }
            }
        }
        .background(Color.sgBgWarm)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button(action: { showShareSheet = true }) {
                    Image(systemName: "square.and.arrow.up")
                }
                Button(action: { router.navigate(to: .editVehicle) }) {
                    Image(systemName: "square.and.pencil")
                }
            }
        }
        .sheet(isPresented: $showShareSheet) {
            ShareVehicleSheet(
                vehicleName: "Beat Hitam",
                onDismiss: { showShareSheet = false },
                onCopy: { showShareSheet = false },
                onExportImage: {
                    showShareSheet = false
                    router.navigate(to: .shareImageCard)
                },
                onSystemShare: { showShareSheet = false }
            )
            .presentationDetents([.medium])
        }
    }
}

private struct VehicleHeroCard: View {
    private let accent = Color(red: 0.18, green: 0.55, blue: 0.34)

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("HONDA · 2022")
                        .font(.custom("PlusJakartaSans-Bold", size: 11))
                        .kerning(0.5)
                        .foregroundColor(.sgTextMuted)
                    Text("Beat Hitam")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                        .foregroundColor(.sgTextPrimary)
                        .kerning(-0.4)
                    Text("B 4521 KZA")
                        .font(.system(size: 13, weight: .semibold, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                ZStack {
                    RoundedRectangle(cornerRadius: 20, style: .continuous)
                        .fill(accent.opacity(0.13))
                        .frame(width: 80, height: 80)
                    Text("\u{f21c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 50))
                        .foregroundColor(accent)
                }
            }
            .padding(.bottom, 14)

            Rectangle().fill(accent.opacity(0.13)).frame(height: 1)
                .padding(.bottom, 14)

            HStack(spacing: 8) {
                StatTile(label: "KM", value: "18.420")
                StatTile(label: "Servis", value: "5x")
                StatTile(label: "Total", value: "Rp 3.310k")
            }
        }
        .padding(20)
        .background(
            LinearGradient(
                colors: [accent.opacity(0.13), accent.opacity(0.05)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 24))
        .overlay(
            RoundedRectangle(cornerRadius: 24)
                .strokeBorder(accent.opacity(0.2), lineWidth: 1)
        )
    }
}

private struct StatTile: View {
    let label: String
    let value: String

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-Bold", size: 10))
                .kerning(0.5)
                .foregroundColor(.sgTextMuted)
            Text(value)
                .font(.system(size: 14, weight: .bold, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct DetailSectionLabel: View {
    let text: String

    var body: some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
            .padding(.horizontal, 20)
            .padding(.bottom, 6)
    }
}

private struct ComponentsSectionHeader: View {
    let total: Int
    let subLabel: String
    let onManage: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            Text("KOMPONEN (\(total))")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
            if !subLabel.isEmpty {
                SubtypeBadge(label: subLabel)
            }
            Spacer()
            Button(action: onManage) {
                HStack(spacing: 4) {
                    Text("Kelola")
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(.sgPrimary)
                    Text("\u{f054}")
                        .font(.custom("FontAwesome6Free-Solid", size: 10))
                        .foregroundColor(.sgPrimary)
                }
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 8)
    }
}

private struct ComponentsGrid: View {
    let components: [ComponentInfo]
    let vehicleType: String
    let onOpenComponent: (String) -> Void
    let onAddComponent: () -> Void

    var body: some View {
        LazyVGrid(columns: [.init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8)], spacing: 8) {
            ForEach(components) { c in
                let urgency: ReminderUrgency = (c.id == "oli_mesin") ? .overdue : .ok
                Button(action: { onOpenComponent(c.id) }) {
                    ComponentCard(component: c, interval: c.interval(for: vehicleType), urgency: urgency)
                }
                .buttonStyle(.plain)
            }
            Button(action: onAddComponent) {
                AddComponentCard()
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, 16)
    }
}

private struct ComponentCard: View {
    let component: ComponentInfo
    let interval: String
    let urgency: ReminderUrgency

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                IconBadge(
                    iconUnicode: component.iconUnicode,
                    foreground: component.color,
                    background: component.color.opacity(0.13),
                    size: 32, iconSize: 18, corner: 8
                )
                Spacer()
                StatusDot(urgency: urgency)
            }
            Text(component.label)
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(.sgTextPrimary)
            Text(interval)
                .font(.custom("PlusJakartaSans-Medium", size: 10))
                .foregroundColor(.sgTextMuted)
        }
        .padding(12)
        .frame(maxWidth: .infinity, minHeight: 86, alignment: .leading)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct AddComponentCard: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ZStack {
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.sgSurfaceAlt)
                    .frame(width: 32, height: 32)
                Text("\u{2b}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextPrimary)
            }
            Text("Tambah komponen")
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(.sgTextPrimary)
            Text("katalog / manual")
                .font(.custom("PlusJakartaSans-Medium", size: 10))
                .foregroundColor(.sgTextMuted)
        }
        .padding(12)
        .frame(maxWidth: .infinity, minHeight: 86, alignment: .leading)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .dashedBorder(cornerRadius: 14, lineWidth: 1.5, color: Color.sgBorder.opacity(1.4))
    }
}

private struct LastServicesList: View {
    var body: some View {
        VStack(spacing: 8) {
            ServiceLine(icon: "\u{f613}", title: "Ganti Oli Mesin", subtitle: "20 Feb 2026 · 16.000 km", cost: "Rp 65.000", accent: Color(red: 0.91, green: 0.61, blue: 0.18))
            ServiceLine(icon: "\u{f0e7}", title: "Busi & Tune Up", subtitle: "10 Des 2025 · 13.800 km", cost: "Rp 45.000", accent: Color(red: 0.91, green: 0.71, blue: 0.18))
        }
        .padding(.horizontal, 16)
    }
}

private struct ServiceLine: View {
    let icon: String
    let title: String
    let subtitle: String
    let cost: String
    let accent: Color

    var body: some View {
        HStack(spacing: 12) {
            IconBadge(
                iconUnicode: icon,
                foreground: accent,
                background: accent.opacity(0.13)
            )
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text(subtitle)
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
            Text(cost)
                .font(.system(size: 13, weight: .bold, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
        }
        .padding(14)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

#Preview {
    NavigationStack {
        VehicleDetailView()
    }
    .environment(AppRouter())
}
