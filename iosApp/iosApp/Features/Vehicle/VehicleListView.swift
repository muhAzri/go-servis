import SwiftUI
import Shared

struct VehicleListView: View {
    var onBack: () -> Void = {}
    var onOpenVehicle: (String) -> Void = { _ in }
    var onAddVehicle: () -> Void = {}

    @ObservedObject private var listModel = VehicleListModel.shared
    @State private var sortKey: String = "input"
    @State private var showSortSheet: Bool = false

    private let sortLabels: [(String, String)] = [
        ("input", "Urutan input"),
        ("az", "A → Z"),
        ("km_asc", "KM terendah"),
        ("km_desc", "KM tertinggi"),
        ("status", "Status (telat dulu)"),
    ]

    private var sortedVehicles: [Vehicle] {
        let base = listModel.state.vehicles
        switch sortKey {
        case "az":
            return base.sorted { $0.displayTitle.lowercased() < $1.displayTitle.lowercased() }
        case "km_asc":
            return base.sorted { $0.odometer < $1.odometer }
        case "km_desc":
            return base.sorted { $0.odometer > $1.odometer }
        default:
            return base
        }
    }

    private var currentSortLabel: String {
        sortLabels.first { $0.0 == sortKey }?.1 ?? "Urutan input"
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack(alignment: .center) {
                VStack(alignment: .leading, spacing: 2) {
                    Text("\(sortedVehicles.count) kendaraan")
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                    Text("Total \(totalKm()) km terakumulasi")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextSubtle)
                }
                Spacer()
                SortChip(label: currentSortLabel, onTap: { showSortSheet = true })
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 12)

            ScrollView {
                LazyVStack(spacing: 10) {
                    ForEach(sortedVehicles, id: \.id) { v in
                        VehicleListRow(vehicle: v) { onOpenVehicle(v.id) }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 88)
            }
        }
        .background(Color.sgBgWarm)
        .overlay(alignment: .bottomTrailing) {
            Button(action: onAddVehicle) {
                HStack(spacing: 8) {
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.white)
                    Text("Tambah Kendaraan")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 20)
                .frame(height: 52)
                .background(Color.sgPrimary)
                .clipShape(Capsule())
                .shadow(color: Color.sgPrimary.opacity(0.35), radius: 12, y: 6)
            }
            .buttonStyle(.plain)
            .padding(20)
        }
        .sheet(isPresented: $showSortSheet) {
            SortPickerSheet(
                title: "Urutkan",
                subtitle: "Pilih cara menampilkan kendaraan",
                options: sortLabels.map { (id, label) in SortOption(id: id, label: label) },
                selectedId: sortKey,
                onPick: { sortKey = $0; showSortSheet = false },
                onCancel: { showSortSheet = false }
            )
            .presentationDetents([.medium])
        }
        .navigationTitle("Garasi Saya")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.primary)
                }
            }

            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onAddVehicle) {
                    Image(systemName: "plus")
                        .font(.system(size: 16, weight: .semibold))
                }
            }
        }
    }

    private func totalKm() -> String {
        let total: Int64 = sortedVehicles.reduce(0) { acc, v in acc + v.odometer }
        return formatKm(total)
    }
}

private struct VehicleListRow: View {
    let vehicle: Vehicle
    let onTap: () -> Void

    private var accent: Color {
        Color(hex: PresentationFactory.shared.vehicleColorHex(vehicle: vehicle)) ?? .sgPrimary
    }

    private var iconUnicode: String {
        vehicle.type == .mobil ? "\u{f1b9}" : "\u{f21c}"
    }

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                ZStack {
                    RoundedRectangle(cornerRadius: 14)
                        .fill(accent.opacity(0.14))
                        .frame(width: 56, height: 56)
                    Text(iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 28))
                        .foregroundColor(accent)
                }
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(vehicle.displayTitle)
                            .font(.custom("PlusJakartaSans-ExtraBold", size: 15))
                            .foregroundColor(.sgTextPrimary)
                        Spacer()
                        StatusPill(urgency: .ok)
                    }
                    Text(vehicle.plateNumber)
                        .font(.system(size: 12, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
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
    }
}

struct SortOption: Identifiable {
    let id: String
    let label: String
}

struct SortPickerSheet: View {
    let title: String
    let subtitle: String
    let options: [SortOption]
    let selectedId: String
    let onPick: (String) -> Void
    let onCancel: () -> Void

    @State private var draft: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)
                .padding(.horizontal, 20)
                .padding(.top, 18)
            Text(subtitle)
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.sgTextMuted)
                .padding(.horizontal, 20)
                .padding(.top, 2)
                .padding(.bottom, 14)

            ScrollView {
                VStack(spacing: 8) {
                    ForEach(options) { opt in
                        Button(action: { draft = opt.id }) {
                            HStack {
                                ZStack {
                                    Circle()
                                        .strokeBorder(draft == opt.id ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                                        .frame(width: 22, height: 22)
                                    if draft == opt.id {
                                        Circle().fill(Color.sgPrimary).frame(width: 12, height: 12)
                                    }
                                }
                                Text(opt.label)
                                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                    .foregroundColor(.sgTextPrimary)
                                Spacer()
                            }
                            .padding(14)
                            .background(draft == opt.id ? Color.sgPrimarySoft : Color.sgSurfaceAlt)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 8)
            }

            VStack(spacing: 8) {
                Button(action: { onPick(draft) }) {
                    Text("Terapkan")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.sgPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .buttonStyle(.plain)
                Button(action: onCancel) {
                    Text("Batal")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .frame(maxWidth: .infinity)
                        .frame(height: 44)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
        .onAppear { draft = selectedId }
    }
}

private extension Color {
    init?(hex: String) {
        let trimmed = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        guard Scanner(string: trimmed).scanHexInt64(&int) else { return nil }
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8) & 0xFF) / 255
        let b = Double(int & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}
