import SwiftUI

struct HistoryView: View {
    var onOpenServiceDetail: () -> Void = {}
    var onAddService: () -> Void = {}
    var isEmpty: Bool = false
    var isLoading: Bool = false
    var isRefreshing: Bool = false

    @State private var selectedVehicleFilter: String = "Semua kendaraan"
    @State private var searchQuery: String = ""
    @State private var sortKey: String = "date_desc"
    @State private var showSortSheet: Bool = false
    @State private var showFilterSheet: Bool = false

    private let sortOptions: [(String, String)] = [
        ("date_desc", "Terbaru dulu"),
        ("date_asc", "Terlama dulu"),
        ("cost_desc", "Termahal dulu"),
        ("cost_asc", "Termurah dulu"),
        ("vehicle", "Per kendaraan"),
    ]

    private var sortLabel: String {
        sortOptions.first { $0.0 == sortKey }?.1 ?? "Terbaru"
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
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(
                    subtitle: "5 servis tercatat · Total Rp 3.310.000",
                    title: "Riwayat Servis"
                )

                StickySearchHeader(
                    placeholder: "Cari servis, bengkel, kendaraan…",
                    text: $searchQuery
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

                VehicleFilterScroller(selected: $selectedVehicleFilter)
                    .padding(.bottom, 14)

                if isRefreshing {
                    PullRefreshIndicator(state: .refreshing)
                }

                MonthSeparator(label: "Mei 2026")
                HistoryRowCard(
                    iconUnicode: "\u{f0ad}",
                    accent: Color(red: 0.36, green: 0.39, blue: 0.34),
                    title: "Servis Berkala",
                    vehicle: "Vario Merah",
                    km: "6.000 km",
                    place: "AHASS Kalimalang",
                    note: "KPB ke-2",
                    cost: "Rp 320.000",
                    date: "1 Mei 2026",
                    onTap: onOpenServiceDetail
                )

                MonthSeparator(label: "Maret 2026")
                HistoryRowCard(
                    iconUnicode: "\u{f613}",
                    accent: Color(red: 0.91, green: 0.61, blue: 0.18),
                    title: "Ganti Oli Mesin",
                    vehicle: "Beat Hitam",
                    km: "16.000 km",
                    place: "AHASS Kebon Jeruk",
                    note: "AHM MPX2 0.8L",
                    cost: "Rp 65.000",
                    date: "20 Feb 2026",
                    onTap: onOpenServiceDetail
                )

                MonthSeparator(label: "Februari 2026")
                HistoryRowCard(
                    iconUnicode: "\u{f613}",
                    accent: Color(red: 0.91, green: 0.61, blue: 0.18),
                    title: "Ganti Oli Mesin",
                    vehicle: "Beat Hitam",
                    km: "16.000 km",
                    place: "AHASS Kebon Jeruk",
                    note: "AHM MPX2 0.8L",
                    cost: "Rp 65.000",
                    date: "20 Feb 2026",
                    onTap: onOpenServiceDetail
                )

                NativeAdCard()
                    .padding(.top, 4)
                    .padding(.bottom, 4)

                MonthSeparator(label: "Januari 2026")
                HistoryRowCard(
                    iconUnicode: "\u{f613}",
                    accent: Color(red: 0.91, green: 0.61, blue: 0.18),
                    title: "Ganti Oli Mesin",
                    vehicle: "Avanza Putih",
                    km: "57.500 km",
                    place: "Auto2000 Cikarang",
                    note: "Motul 5W-30 4L + filter",
                    cost: "Rp 480.000",
                    date: "5 Jan 2026",
                    onTap: onOpenServiceDetail
                )

                MonthSeparator(label: "Desember 2025")
                HistoryRowCard(
                    iconUnicode: "\u{f0e7}",
                    accent: Color(red: 0.91, green: 0.71, blue: 0.18),
                    title: "Busi & Tune Up",
                    vehicle: "Beat Hitam",
                    km: "13.800 km",
                    place: "Bengkel Pak Karto",
                    note: "NGK CPR8EA",
                    cost: "Rp 45.000",
                    date: "10 Des 2025",
                    onTap: onOpenServiceDetail
                )

                MonthSeparator(label: "September 2025")
                HistoryRowCard(
                    iconUnicode: "\u{f1cd}",
                    accent: Color(red: 0.25, green: 0.30, blue: 0.36),
                    title: "Rotasi/Ganti Ban",
                    vehicle: "Avanza Putih",
                    km: "50.000 km",
                    place: "Bridgestone Bekasi",
                    note: "Turanza 185/65 R15 4 pcs",
                    cost: "Rp 2.400.000",
                    date: "22 Sep 2025",
                    onTap: onOpenServiceDetail
                )
                .padding(.bottom, 24)
            }
        }
        .sheet(isPresented: $showSortSheet) {
            HistorySortSheet(
                options: sortOptions,
                selectedKey: sortKey,
                onPick: { sortKey = $0; showSortSheet = false },
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

// MARK: - Subviews

private struct VehicleFilterScroller: View {
    @Binding var selected: String

    private let options = ["Semua kendaraan", "Beat Hitam", "Vario Merah", "Avanza Putih", "Brio Biru"]

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(options, id: \.self) { o in
                    Button { selected = o } label: {
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
    let iconUnicode: String
    let accent: Color
    let title: String
    let vehicle: String
    let km: String
    let place: String
    let note: String
    let cost: String
    let date: String
    var onTap: () -> Void = {}

    var body: some View {
        Button(action: onTap) {
            HStack(alignment: .top, spacing: 12) {
                IconBadge(
                    iconUnicode: iconUnicode,
                    foreground: accent,
                    background: accent.opacity(0.13)
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
                    Text("\(vehicle) · \(km)")
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
