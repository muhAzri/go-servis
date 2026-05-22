import SwiftUI

struct HistoryFilterValue: Equatable {
    var vehicleIds: Set<String> = []
    var serviceTypeIds: Set<String> = []
    var componentIds: Set<String> = []
    var workshopIds: Set<String> = []
    var dateRangeId: String = "all"
}

struct HistoryFilterSheet: View {
    var initial: HistoryFilterValue = HistoryFilterValue()
    var resultCount: Int = 12
    var onDismiss: () -> Void = {}
    var onApply: (HistoryFilterValue) -> Void = { _ in }

    @State private var draft: HistoryFilterValue = HistoryFilterValue()

    private let vehicleItems: [FilterRow] = [
        .init(id: "v1", label: "Beat Hitam", sub: "B 4521 KZA"),
        .init(id: "v2", label: "Vario Merah", sub: "B 6789 SKR"),
        .init(id: "v3", label: "Avanza Putih", sub: "B 1234 ABC"),
        .init(id: "v4", label: "Brio Biru", sub: "B 9876 XYZ"),
    ]
    private let serviceItems: [FilterRow] = [
        .init(id: "oli", label: "Ganti Oli"),
        .init(id: "filter", label: "Filter"),
        .init(id: "rem", label: "Kampas Rem"),
        .init(id: "ban", label: "Ban"),
        .init(id: "aki", label: "Aki"),
        .init(id: "radiator", label: "Coolant"),
    ]
    private let componentItems: [FilterRow] = [
        .init(id: "c_oil", label: "Oli Mesin"),
        .init(id: "c_oilf", label: "Filter Oli"),
        .init(id: "c_brake", label: "Kampas Rem"),
    ]
    private let workshopItems: [FilterRow] = [
        .init(id: "w1", label: "AHASS Sentral"),
        .init(id: "w2", label: "Auto2000 Cibubur"),
        .init(id: "w3", label: "Bengkel Pak Sukar"),
    ]
    private let dateRanges: [(String, String)] = [
        ("all", "Semua"), ("month", "Bulan ini"), ("3m", "3 bulan"),
        ("6m", "6 bulan"), ("1y", "1 tahun"), ("custom", "Custom"),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text("Filter Riwayat")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
                Button(action: { draft = HistoryFilterValue() }) {
                    Text("Reset semua")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgPrimary)
                }
            }
            .padding(.horizontal, 20)
            .padding(.top, 18)
            .padding(.bottom, 8)

            Divider().background(Color.sgBorder)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    section(title: "Kendaraan", items: vehicleItems, selected: draft.vehicleIds) { draft.vehicleIds = $0 }
                    section(title: "Jenis servis", items: serviceItems, selected: draft.serviceTypeIds) { draft.serviceTypeIds = $0 }
                    section(title: "Komponen", items: componentItems, selected: draft.componentIds) { draft.componentIds = $0 }
                    section(title: "Bengkel", items: workshopItems, selected: draft.workshopIds) { draft.workshopIds = $0 }

                    Text("Range tanggal".uppercased())
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                        .kerning(1)
                        .foregroundColor(.sgTextMuted)

                    HStack(spacing: 6) {
                        ForEach(dateRanges, id: \.0) { r in
                            Button(action: { draft.dateRangeId = r.0 }) {
                                Text(r.1)
                                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                                    .foregroundColor(draft.dateRangeId == r.0 ? .sgPrimary : .sgTextMuted)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 8)
                                    .background(draft.dateRangeId == r.0 ? Color.sgPrimarySoft : Color.sgSurfaceAlt)
                                    .clipShape(Capsule())
                                    .overlay(
                                        Capsule().strokeBorder(
                                            draft.dateRangeId == r.0 ? Color.sgPrimary : Color.sgBorder,
                                            lineWidth: 1
                                        )
                                    )
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .padding(20)
            }

            Divider().background(Color.sgBorder)

            Button(action: { onApply(draft); onDismiss() }) {
                HStack(spacing: 8) {
                    Text("\u{f00c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                        .foregroundColor(.white)
                    Text("Terapkan (\(resultCount) hasil)")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.white)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(Color.sgPrimary)
                .clipShape(Capsule())
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(Color.sgSurface)
        }
        .background(Color.sgSurface)
        .onAppear { draft = initial }
    }

    private func section(
        title: String,
        items: [FilterRow],
        selected: Set<String>,
        update: @escaping (Set<String>) -> Void
    ) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
            VStack(spacing: 0) {
                ForEach(items) { item in
                    Button(action: {
                        var s = selected
                        if s.contains(item.id) { s.remove(item.id) } else { s.insert(item.id) }
                        update(s)
                    }) {
                        HStack(spacing: 12) {
                            ZStack {
                                RoundedRectangle(cornerRadius: 6)
                                    .strokeBorder(selected.contains(item.id) ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                                    .background(
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(selected.contains(item.id) ? Color.sgPrimary : Color.clear)
                                    )
                                    .frame(width: 22, height: 22)
                                if selected.contains(item.id) {
                                    Text("\u{f00c}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                                        .foregroundColor(.white)
                                }
                            }
                            VStack(alignment: .leading, spacing: 2) {
                                Text(item.label)
                                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                    .foregroundColor(.sgTextPrimary)
                                if let sub = item.sub {
                                    Text(sub)
                                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                                        .foregroundColor(.sgTextMuted)
                                }
                            }
                            Spacer()
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 10)
                    }
                    .buttonStyle(.plain)
                    if item.id != items.last?.id {
                        Divider().background(Color.sgBorder).padding(.leading, 48)
                    }
                }
            }
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
    }
}

struct FilterRow: Identifiable {
    let id: String
    let label: String
    var sub: String? = nil
}
