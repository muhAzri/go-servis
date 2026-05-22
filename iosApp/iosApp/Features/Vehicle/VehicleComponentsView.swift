import SwiftUI

struct VehicleComponentsView: View {
    var vehicleType: String = "motor"
    var subtype: String = "matic"
    var vehicleName: String = "Beat Hitam"
    var isEmpty: Bool = false
    var isLoading: Bool = false

    @Environment(AppRouter.self) private var router

    @State private var currentSubtype: String = "matic"
    @State private var showSubtypeSheet: Bool = false
    @State private var activeCategory: String = "all"

    private var allComponents: [ComponentInfo] {
        ComponentsCatalog.forSubtype(currentSubtype)
    }

    private var components: [ComponentInfo] {
        activeCategory == "all" ? allComponents : allComponents.filter { categoryOf($0.id) == activeCategory }
    }

    private func categoryOf(_ id: String) -> String {
        switch id {
        case "oli_mesin", "busi", "filter_udara", "filter_oli", "tune_up", "timing_belt", "vbelt", "roller": return "mesin"
        case "aki": return "kelistrikan"
        case "ban", "kampas_rem", "shock", "rantai", "kampas_kopling", "oli_kopling": return "kaki"
        case "radiator", "oli_gardan", "minyak_rem", "wiper", "filter_ac": return "pendingin"
        default: return "mesin"
        }
    }

    private var categoryChips: [FilterChipItem] {
        let mesinCount = allComponents.filter { categoryOf($0.id) == "mesin" }.count
        let listrikCount = allComponents.filter { categoryOf($0.id) == "kelistrikan" }.count
        let kakiCount = allComponents.filter { categoryOf($0.id) == "kaki" }.count
        let pendinginCount = allComponents.filter { categoryOf($0.id) == "pendingin" }.count
        var result: [FilterChipItem] = [
            FilterChipItem(id: "all", label: "Semua", count: allComponents.count),
        ]
        if mesinCount > 0 { result.append(FilterChipItem(id: "mesin", label: "Mesin", count: mesinCount)) }
        if listrikCount > 0 { result.append(FilterChipItem(id: "kelistrikan", label: "Kelistrikan", count: listrikCount)) }
        if kakiCount > 0 { result.append(FilterChipItem(id: "kaki", label: "Kaki-kaki", count: kakiCount)) }
        if pendinginCount > 0 { result.append(FilterChipItem(id: "pendingin", label: "Pendingin", count: pendinginCount)) }
        return result
    }

    private var subLabel: String {
        VehicleSubtypes.label(for: vehicleType, id: currentSubtype)
    }

    private var typeLabel: String {
        vehicleType == "mobil" ? "Mobil" : "Motor"
    }

    var body: some View {
        Group {
            if isLoading {
                VStack(spacing: 12) {
                    Skeleton.Tile(count: 6)
                    Skeleton.Row(leading: .icon)
                    Skeleton.Row(leading: .icon)
                    Spacer()
                }
                .padding(.top, 12)
            } else if isEmpty {
                VStack(spacing: 0) {
                    SubtypeBanner(subLabel: subLabel, typeLabel: typeLabel, onChange: { showSubtypeSheet = true })
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                        .padding(.bottom, 12)
                    EmptyState(
                        iconUnicode: "\u{f0ad}",
                        title: "Belum ada komponen dipantau",
                        body: "Pilih komponen yang ingin kamu pantau usianya — kami pakai interval pabrikan.",
                        ctaLabel: "Pilih Komponen",
                        onCta: { router.navigate(to: .addCustomComponent) }
                    )
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            } else {
                scrollContent
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Komponen")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { currentSubtype = subtype }
        .sheet(isPresented: $showSubtypeSheet) {
            SubtypePickerSheet(
                vehicleType: vehicleType,
                vehicleName: vehicleName,
                selectedId: currentSubtype,
                onPick: { newId in
                    currentSubtype = newId
                    showSubtypeSheet = false
                },
                onDismiss: { showSubtypeSheet = false }
            )
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack(spacing: 1) {
                    Text("Komponen")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 16))
                        .foregroundColor(.sgTextPrimary)
                    Text("\(vehicleName) · \(allComponents.count) dipantau")
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

    private var scrollContent: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                SubtypeBanner(subLabel: subLabel, typeLabel: typeLabel, onChange: { showSubtypeSheet = true })
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 12)

                FilterChipBar(items: categoryChips, activeId: $activeCategory)
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

private struct SubtypePickerSheet: View {
    let vehicleType: String
    let vehicleName: String
    let selectedId: String
    let onPick: (String) -> Void
    let onDismiss: () -> Void

    @State private var draft: String = ""

    private var options: [VehicleSubtype] {
        VehicleSubtypes.list(for: vehicleType)
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Pilih sub-tipe")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                        .foregroundColor(.sgTextPrimary)
                        .kerning(-0.3)
                    Text("Untuk \(vehicleName)")
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Button(action: onDismiss) {
                    Text("\u{f00d}")
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 32, height: 32)
                        .background(Color.sgSurfaceAlt)
                        .clipShape(Circle())
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 20)
            .padding(.top, 16)
            .padding(.bottom, 14)

            ScrollView {
                VStack(spacing: 8) {
                    ForEach(options) { opt in
                        SubtypeRow(
                            option: opt,
                            active: draft == opt.id,
                            onTap: { draft = opt.id }
                        )
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 12)
            }

            VStack(spacing: 8) {
                Button(action: { onPick(draft) }) {
                    Text("Pilih")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.sgPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .buttonStyle(.plain)
                Button(action: onDismiss) {
                    Text("Batal")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .frame(maxWidth: .infinity)
                        .frame(height: 44)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 24)
            .background(Color.sgSurface)
        }
        .background(Color.sgSurface)
        .onAppear { draft = selectedId }
    }
}

private struct SubtypeRow: View {
    let option: VehicleSubtype
    let active: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 14) {
                ZStack {
                    Circle()
                        .strokeBorder(active ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                        .frame(width: 22, height: 22)
                    if active {
                        Circle()
                            .fill(Color.sgPrimary)
                            .frame(width: 12, height: 12)
                    }
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(option.label)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(option.sample)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
            }
            .padding(14)
            .background(active ? Color.sgPrimarySoft : Color.sgSurfaceAlt)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(active ? Color.sgPrimary : Color.clear, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    NavigationStack { VehicleComponentsView() }
        .environment(AppRouter())
}
