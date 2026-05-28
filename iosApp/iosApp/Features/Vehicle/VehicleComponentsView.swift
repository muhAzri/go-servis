import SwiftUI
import Shared

struct VehicleComponentsView: View {
    let vehicleId: String
    var onOpenTracked: (String) -> Void = { _ in }
    var onOpenCatalog: (String) -> Void = { _ in }
    var onAdd: () -> Void = {}

    @StateObject private var model = VehicleComponentsModel()
    @State private var activeCategory: String = "all"

    private var state: VehicleComponentsViewModel.UiState { model.state }
    private var vehicle: Shared.Vehicle? { state.vehicle }
    private var vehicleType: VehicleType { vehicle?.type ?? VehicleType.motor }
    private var vehicleTypeKey: String { vehicleType == .mobil ? "mobil" : "motor" }
    private var subtypeId: String {
        let raw = vehicle?.subtypeId ?? ""
        if raw.isEmpty || raw == "*" {
            return VehicleSubtypes.defaultId(for: vehicleTypeKey)
        }
        return raw
    }
    private var subLabel: String { VehicleSubtypes.label(for: vehicleTypeKey, id: subtypeId) }
    private var typeLabel: String { vehicleType == .mobil ? "Mobil" : "Motor" }
    private var vehicleName: String { vehicle?.displayTitle ?? "—" }

    private var items: [VehicleComponentsViewModel.TrackedItem] { state.items }
    private var filteredItems: [VehicleComponentsViewModel.TrackedItem] {
        if activeCategory == "all" { return items }
        return items.filter { categoryOf($0.tracked.catalogComponentId) == activeCategory }
    }

    private func categoryOf(_ catalogId: String) -> String {
        switch catalogId {
        case "oli_mesin", "busi", "filter_udara", "filter_oli", "tune_up", "timing_belt", "vbelt", "roller": return "mesin"
        case "aki": return "kelistrikan"
        case "ban", "kampas_rem", "shock", "rantai", "kampas_kopling", "oli_kopling": return "kaki"
        case "radiator", "oli_gardan", "minyak_rem", "wiper", "filter_ac": return "pendingin"
        default: return "mesin"
        }
    }

    private var categoryChips: [FilterChipItem] {
        let mesinCount = items.filter { categoryOf($0.tracked.catalogComponentId) == "mesin" }.count
        let listrikCount = items.filter { categoryOf($0.tracked.catalogComponentId) == "kelistrikan" }.count
        let kakiCount = items.filter { categoryOf($0.tracked.catalogComponentId) == "kaki" }.count
        let pendinginCount = items.filter { categoryOf($0.tracked.catalogComponentId) == "pendingin" }.count
        var result: [FilterChipItem] = [
            FilterChipItem(id: "all", label: "Semua", count: items.count),
        ]
        if mesinCount > 0 { result.append(FilterChipItem(id: "mesin", label: "Mesin", count: mesinCount)) }
        if listrikCount > 0 { result.append(FilterChipItem(id: "kelistrikan", label: "Kelistrikan", count: listrikCount)) }
        if kakiCount > 0 { result.append(FilterChipItem(id: "kaki", label: "Kaki-kaki", count: kakiCount)) }
        if pendinginCount > 0 { result.append(FilterChipItem(id: "pendingin", label: "Pendingin", count: pendinginCount)) }
        return result
    }

    var body: some View {
        Group {
            if state.isLoading {
                VStack(spacing: 12) {
                    Skeleton.Tile(count: 6)
                    Skeleton.Row(leading: .icon)
                    Skeleton.Row(leading: .icon)
                    Spacer()
                }
                .padding(.top, 12)
            } else if state.isEmpty {
                VStack(spacing: 0) {
                    SubtypeBanner(subLabel: subLabel, typeLabel: typeLabel, vehicleType: vehicleType)
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                        .padding(.bottom, 12)
                    EmptyState(
                        iconUnicode: "\u{f0ad}",
                        title: "Belum ada komponen dipantau",
                        body: "Pilih dari rekomendasi sesuai \(subLabel) — atur intervalnya, lalu pantau.",
                        ctaLabel: "Pilih Komponen",
                        onCta: onAdd
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
        .onAppear { model.load(vehicleId: vehicleId) }
        .toolbar {
            ToolbarItem(placement: .principal) {
                VStack(spacing: 1) {
                    Text("Komponen")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 16))
                        .foregroundColor(.sgTextPrimary)
                    Text("\(vehicleName) · \(items.count) dipantau")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onAdd) {
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 18))
                        .foregroundColor(.sgPrimary)
                }
            }
        }
    }

    private var scrollContent: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                SubtypeBanner(subLabel: subLabel, typeLabel: typeLabel, vehicleType: vehicleType)
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 12)

                FilterChipBar(items: categoryChips, activeId: $activeCategory)
                    .padding(.bottom, 12)

                TrackedComponentsList(
                    items: filteredItems,
                    vehicleType: vehicleType,
                    onOpenTracked: onOpenTracked,
                    onOpenCatalog: onOpenCatalog
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 12)

                AddComponentRow(onAdd: onAdd)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
            }
        }
    }
}

private struct SubtypeBanner: View {
    let subLabel: String
    let typeLabel: String
    let vehicleType: VehicleType

    private let accent = Color(red: 0.18, green: 0.55, blue: 0.34)
    private var icon: String { vehicleType == .mobil ? "\u{f1b9}" : "\u{f21c}" }

    var body: some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(accent.opacity(0.13))
                    .frame(width: 44, height: 44)
                Text(icon)
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

private struct TrackedComponentsList: View {
    let items: [VehicleComponentsViewModel.TrackedItem]
    let vehicleType: VehicleType
    let onOpenTracked: (String) -> Void
    let onOpenCatalog: (String) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(items.enumerated()), id: \.element.tracked.id) { index, item in
                if index > 0 {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
                Button(action: {
                    let trackedId = item.tracked.id
                    if !trackedId.isEmpty {
                        onOpenTracked(trackedId)
                    } else {
                        onOpenCatalog(item.tracked.catalogComponentId)
                    }
                }) {
                    TrackedComponentRow(item: item, vehicleType: vehicleType)
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
}

private struct TrackedComponentRow: View {
    let item: VehicleComponentsViewModel.TrackedItem
    let vehicleType: VehicleType

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    private var icon: String { item.catalog?.iconUnicode ?? "\u{f0ad}" }
    private var color: Color { item.catalog?.uiColor ?? .sgTextMuted }
    private var intervalText: String {
        if let kmOverride = item.tracked.intervalKmOverride {
            return "\(formatThousands(Int(truncating: kmOverride))) km"
        }
        return item.catalog?.intervalLabel(for: vehicleType) ?? "—"
    }
    private var lastText: String {
        var parts: [String] = []
        if let dist = item.tracked.lastServiceOdometer as? KotlinLong {
            parts.append("\(formatThousands(Int(truncating: dist))) km")
        }
        if let raw = item.tracked.lastServiceDate {
            let date = Date(timeIntervalSince1970: TimeInterval(truncating: raw) / 1000.0)
            parts.append(Self.dateFormatter.string(from: date))
        }
        return parts.isEmpty ? "belum tercatat" : parts.joined(separator: " · ")
    }

    var body: some View {
        HStack(spacing: 12) {
            IconBadge(
                iconUnicode: icon,
                foreground: color,
                background: color.opacity(0.13),
                size: 38, iconSize: 20, corner: 10
            )
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 6) {
                    Text(item.displayName)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    if item.urgency == .overdue {
                        StatusDot(urgency: .overdue)
                    }
                }
                Text("Terakhir: \(lastText)")
                    .font(.custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 1) {
                Text("INTERVAL")
                    .font(.custom("PlusJakartaSans-Bold", size: 10))
                    .kerning(0.5)
                    .foregroundColor(.sgTextSubtle)
                Text(intervalText)
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

private func formatThousands(_ value: Int) -> String {
    if value == 0 { return "0" }
    let formatter = NumberFormatter()
    formatter.numberStyle = .decimal
    formatter.groupingSeparator = "."
    formatter.locale = Locale(identifier: "id_ID")
    return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
}
