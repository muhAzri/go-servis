import SwiftUI
import Shared

struct HistoryView: View {
    var onOpenServiceDetail: (String) -> Void = { _ in }
    var onAddService: () -> Void = {}
    var isRefreshing: Bool = false

    @ObservedObject private var services = ServiceHistoryModel.shared
    @ObservedObject private var vehicles = VehicleListModel.shared

    @State private var showSortSheet: Bool = false
    @State private var showFilterSheet: Bool = false

    private let sortOptions: [(String, String)] = [
        ("date_desc", "Terbaru dulu"),
        ("date_asc", "Terlama dulu"),
        ("cost_desc", "Termahal dulu"),
        ("cost_asc", "Termurah dulu"),
        ("vehicle", "Per kendaraan"),
    ]

    private var sortKey: String { sortKeyFor(services.state.sort) }
    private var sortLabel: String {
        sortOptions.first { $0.0 == sortKey }?.1 ?? "Terbaru"
    }
    private var searchQuery: String { services.state.query }
    private var selectedVehicleFilter: String {
        guard let id = services.state.vehicleIds.first,
              let v = vehicles.state.vehicles.first(where: { $0.id == id })
        else { return "Semua kendaraan" }
        return v.displayTitle
    }

    private var isLoading: Bool { services.state.isLoading }
    private var isEmpty: Bool { services.state.isEmpty }
    private var vehicleById: [String: Vehicle] {
        Dictionary(uniqueKeysWithValues: vehicles.state.vehicles.map { ($0.id, $0) })
    }
    private var filterOptions: [String] {
        ["Semua kendaraan"] + vehicles.state.vehicles.map { $0.displayTitle }
    }

    var body: some View {
        if isLoading {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Riwayat Servis")
                Skeleton.Row(leading: .icon)
                Skeleton.Row(leading: .icon)
                Skeleton.Row(leading: .icon)
                Skeleton.Row(leading: .icon)
                Spacer()
            }
        } else if isEmpty {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: "Belum ada catatan", title: "Riwayat Servis")
                EmptyState(
                    iconUnicode: "\u{f0ad}",
                    title: "Belum ada servis tercatat",
                    body: "Catat servis pertama untuk mulai melacak biaya & interval per komponen.",
                    ctaLabel: "Catat Servis",
                    onCta: onAddService
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        } else {
            scrollContent
        }
    }

    private var scrollContent: some View {
        let records = services.state.records
        let totalCount = Int(services.state.totalCount)
        let totalCost = services.state.totalCostIdr
        let groupedKeys = records.map { yearMonthLabel($0.serviceDate) }
        let groupedByMonth = Dictionary(grouping: records, by: { yearMonthLabel($0.serviceDate) })
        let orderedMonthLabels = Array(NSOrderedSet(array: groupedKeys)) as? [String] ?? []

        return ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(
                    subtitle: "\(totalCount) servis tercatat · Total \(formatRupiah(totalCost))",
                    title: "Riwayat Servis"
                )

                StickySearchHeader(
                    placeholder: "Cari servis, bengkel, kendaraan…",
                    text: Binding(
                        get: { searchQuery },
                        set: { services.vm.setQuery(query: $0) }
                    )
                )

                HStack(spacing: 8) {
                    SortChip(label: sortLabel) { showSortSheet = true }
                    Button(action: { showFilterSheet = true }) {
                        HStack(spacing: 6) {
                            Text("\u{f0b0}")
                                .font(.custom("FontAwesome6Free-Solid", size: 12))
                                .foregroundColor(.sgTextMuted)
                            Text("Filter")
                                .font(.custom("PlusJakartaSans-Bold", size: 12))
                                .foregroundColor(.sgTextPrimary)
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 7)
                        .background(Color.sgSurface)
                        .clipShape(Capsule())
                        .overlay(Capsule().strokeBorder(Color.sgBorder, lineWidth: 1))
                    }
                    .buttonStyle(.plain)
                    Spacer()
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 4)

                VehicleFilterScroller(
                    options: filterOptions,
                    selected: selectedVehicleFilter,
                    onSelect: { label in
                        let ids: Set<String>
                        if label == "Semua kendaraan" {
                            ids = []
                        } else if let v = vehicles.state.vehicles.first(where: { $0.displayTitle == label }) {
                            ids = [v.id]
                        } else {
                            ids = []
                        }
                        services.vm.setVehicleIds(ids: ids)
                    }
                )
                    .padding(.bottom, 14)

                if isRefreshing {
                    PullRefreshIndicator(state: .refreshing)
                }

                ForEach(Array(orderedMonthLabels.enumerated()), id: \.offset) { idx, label in
                    MonthSeparator(label: label)
                    ForEach(groupedByMonth[label] ?? [], id: \.id) { record in
                        HistoryRowCard(
                            record: record,
                            vehicle: vehicleById[record.vehicleId],
                            onTap: { onOpenServiceDetail(record.id) }
                        )
                    }
                    if idx == 2 {
                        NativeAdCard()
                            .padding(.top, 4)
                            .padding(.bottom, 4)
                    }
                }
                if services.state.canLoadMore {
                    LoadMoreButton(
                        shownCount: records.count,
                        totalCount: totalCount,
                        onTap: { services.vm.loadMore() }
                    )
                }
                Spacer().frame(height: 24)
            }
        }
        .sheet(isPresented: $showSortSheet) {
            HistorySortSheet(
                options: sortOptions,
                selectedKey: sortKey,
                onPick: { key in
                    services.vm.setSort(sort: serviceSortFor(key: key))
                    showSortSheet = false
                },
                onDismiss: { showSortSheet = false }
            )
            .presentationDetents([.medium])
        }
        .sheet(isPresented: $showFilterSheet) {
            HistoryFilterSheet(
                onDismiss: { showFilterSheet = false },
                onApply: { _ in showFilterSheet = false }
            )
            .presentationDetents([.large])
        }
    }
}

private func serviceSortFor(key: String) -> ServiceSort {
    switch key {
    case "date_asc": return ServiceSort.dateasc
    case "cost_desc": return ServiceSort.costdesc
    case "cost_asc": return ServiceSort.costasc
    case "vehicle": return ServiceSort.odometerasc
    default: return ServiceSort.datedesc
    }
}

private func sortKeyFor(_ sort: ServiceSort) -> String {
    if sort == ServiceSort.dateasc { return "date_asc" }
    if sort == ServiceSort.costdesc { return "cost_desc" }
    if sort == ServiceSort.costasc { return "cost_asc" }
    if sort == ServiceSort.odometerasc || sort == ServiceSort.odometerdesc { return "vehicle" }
    return "date_desc"
}

private struct LoadMoreButton: View {
    let shownCount: Int
    let totalCount: Int
    let onTap: () -> Void

    var body: some View {
        HStack {
            Spacer()
            Button(action: onTap) {
                HStack(spacing: 8) {
                    Text("Muat lebih banyak (\(shownCount)/\(totalCount))")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                    Text("\u{f078}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                .padding(.horizontal, 18)
                .padding(.vertical, 10)
                .background(Color.sgSurface)
                .clipShape(Capsule())
                .overlay(Capsule().strokeBorder(Color.sgBorder, lineWidth: 1))
            }
            .buttonStyle(.plain)
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}

private let monthNames = [
    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
]

private func yearMonthLabel(_ epochMillis: Int64) -> String {
    let date = Date(timeIntervalSince1970: TimeInterval(epochMillis) / 1000)
    var calendar = Calendar(identifier: .gregorian)
    calendar.timeZone = TimeZone.current
    let comps = calendar.dateComponents([.year, .month], from: date)
    let month = monthNames[(comps.month ?? 1) - 1]
    return "\(month) \(comps.year ?? 2026)"
}

private func formatShortDate(_ epochMillis: Int64) -> String {
    let date = Date(timeIntervalSince1970: TimeInterval(epochMillis) / 1000)
    var calendar = Calendar(identifier: .gregorian)
    calendar.timeZone = TimeZone.current
    let comps = calendar.dateComponents([.year, .month, .day], from: date)
    let monthShort = String(monthNames[(comps.month ?? 1) - 1].prefix(3))
    return "\(comps.day ?? 1) \(monthShort) \(comps.year ?? 2026)"
}

private func formatRupiah(_ amountIdr: Int64) -> String {
    return "Rp \(formatGroupedLong(amountIdr))"
}

private func serviceIcon(_ record: ServiceRecord) -> String {
    switch record.serviceType.key {
    case "oli": return "\u{f613}"
    case "filter": return "\u{f0b0}"
    case "ban": return "\u{f1cd}"
    case "aki": return "\u{f5df}"
    case "rem": return "\u{f1ce}"
    case "tune_up": return "\u{f0e7}"
    default: return "\u{f0ad}"
    }
}

private struct HistorySortSheet: View {
    let options: [(String, String)]
    let selectedKey: String
    let onPick: (String) -> Void
    let onDismiss: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Urutkan riwayat")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                .foregroundColor(.sgTextPrimary)
                .padding(.horizontal, 20)
                .padding(.top, 18)
                .padding(.bottom, 14)
            ScrollView {
                VStack(spacing: 8) {
                    ForEach(options, id: \.0) { opt in
                        Button(action: { onPick(opt.0) }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    Circle()
                                        .strokeBorder(selectedKey == opt.0 ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                                        .frame(width: 20, height: 20)
                                    if selectedKey == opt.0 {
                                        Circle().fill(Color.sgPrimary).frame(width: 10, height: 10)
                                    }
                                }
                                Text(opt.1)
                                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                    .foregroundColor(.sgTextPrimary)
                                Spacer()
                            }
                            .padding(14)
                            .background(selectedKey == opt.0 ? Color.sgPrimarySoft : Color.sgSurfaceAlt)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }
        }
        .background(Color.sgSurface)
    }
}

private struct VehicleFilterScroller: View {
    let options: [String]
    let selected: String
    let onSelect: (String) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(options, id: \.self) { o in
                    Button { onSelect(o) } label: {
                        Text(o)
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(selected == o ? .white : .sgTextMuted)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 8)
                            .background(selected == o ? Color.sgPrimary : Color.sgSurface)
                            .clipShape(Capsule())
                            .overlay(
                                Capsule().stroke(
                                    selected == o ? .clear : Color.sgBorder,
                                    lineWidth: 1
                                )
                            )
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
        }
    }
}

private struct MonthSeparator: View {
    let label: String
    var body: some View {
        Text(label.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
            .padding(.horizontal, 24)
            .padding(.top, 12)
            .padding(.bottom, 8)
    }
}

private struct HistoryRowCard: View {
    let record: ServiceRecord
    let vehicle: Vehicle?
    var onTap: () -> Void = {}

    private var icon: String { serviceIcon(record) }
    private var title: String { record.serviceType.name }
    private var vehicleLabel: String { vehicle?.displayTitle ?? "—" }
    private var km: String { "\(formatGroupedLong(record.odometer)) km" }
    private var place: String {
        if let workshop = record.workshop, !workshop.isEmpty { return workshop }
        return "—"
    }
    private var note: String { record.note ?? "" }
    private var cost: String { formatRupiah(record.cost) }
    private var date: String { formatShortDate(record.serviceDate) }

    var body: some View {
        Button(action: onTap) {
            HStack(alignment: .top, spacing: 12) {
                IconBadge(
                    iconUnicode: icon,
                    foreground: .sgPrimary,
                    background: Color.sgPrimary.opacity(0.13)
                )

                VStack(alignment: .leading, spacing: 2) {
                    HStack {
                        Text(title)
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Spacer()
                        Text(cost)
                            .font(.system(size: 13, weight: .bold, design: .monospaced))
                            .foregroundColor(.sgTextPrimary)
                    }
                    Text("\(vehicleLabel) · \(km)")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                    HStack(spacing: 6) {
                        Text("\u{f3c5}")
                            .font(.custom("FontAwesome6Free-Solid", size: 11))
                            .foregroundColor(.sgTextSubtle)
                        Text(place)
                            .font(.custom("PlusJakartaSans-Medium", size: 12))
                            .foregroundColor(.sgTextSubtle)
                    }
                    .padding(.top, 2)

                    if !note.isEmpty {
                        Text(note)
                            .font(.system(size: 11, design: .monospaced))
                            .foregroundColor(.sgTextMuted)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(Color.sgSurfaceAlt)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                            .padding(.top, 6)
                    }

                    Text(date)
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextSubtle)
                        .padding(.top, 4)
                }
            }
            .padding(14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .padding(.horizontal, 16)
        .padding(.bottom, 8)
    }
}

#Preview {
    HistoryView()
        .background(Color.sgBgWarm)
}
