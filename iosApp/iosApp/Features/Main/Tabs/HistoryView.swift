import SwiftUI

struct HistoryView: View {
    @State private var selectedVehicleFilter: String = "Semua kendaraan"

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(
                    subtitle: "5 servis tercatat · Total Rp 3.310.000",
                    title: "Riwayat Servis"
                )

                VehicleFilterScroller(selected: $selectedVehicleFilter)
                    .padding(.bottom, 14)

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
                    date: "1 Mei 2026"
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
                    date: "20 Feb 2026"
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
                    date: "20 Feb 2026"
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
                    date: "5 Jan 2026"
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
                    date: "10 Des 2025"
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
                    date: "22 Sep 2025"
                )
                .padding(.bottom, 24)
            }
        }
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

    var body: some View {
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
        .padding(.horizontal, 16)
        .padding(.bottom, 8)
    }
}

#Preview {
    HistoryView()
        .background(Color.sgBgWarm)
}
