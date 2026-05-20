import SwiftUI

// ──────────────────────────────── §D · Empty states ────────────────────────────────

struct HomeViewEmpty: View {
    var vehicleType: String = "motor"
    var onAddVehicle: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            TabHeader(subtitle: nil, title: "Garasi Saya")
            EmptyState(
                iconUnicode: vehicleType == "mobil" ? "\u{f1b9}" : "\u{f21c}",
                title: "Belum ada kendaraan",
                body: "Tambah motor atau mobilmu untuk mulai catat servis & dapat pengingat.",
                ctaLabel: "+ Tambah Kendaraan",
                onCta: onAddVehicle
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
    }
}

struct HistoryViewEmpty: View {
    var onAddService: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            TabHeader(subtitle: nil, title: "Riwayat Servis")
            EmptyState(
                iconUnicode: "\u{f1da}",
                title: "Belum ada servis tercatat",
                body: "Catat servis pertama untuk mulai melacak biaya & interval per komponen.",
                ctaLabel: "Catat Servis",
                onCta: onAddService
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
    }
}

struct RemindersViewEmpty: View {
    var onAddReminder: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            TabHeader(subtitle: nil, title: "Pengingat Servis")
            EmptyState(
                iconUnicode: "\u{f0f3}",
                title: "Belum ada reminder aktif",
                body: "Buat pengingat berdasarkan KM atau tanggal supaya servis tepat waktu.",
                ctaLabel: "+ Buat Pengingat",
                onCta: onAddReminder
            )
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
    }
}

struct VehicleComponentsEmpty: View {
    var onPickComponents: () -> Void = {}

    var body: some View {
        EmptyState(
            iconUnicode: "\u{f0ad}",
            title: "Belum ada komponen dipantau",
            body: "Pilih komponen yang ingin kamu pantau usianya — kami pakai interval pabrikan.",
            ctaLabel: "Pilih Komponen",
            onCta: onPickComponents
        )
        .background(Color.sgBgWarm)
    }
}

// ──────────────────────────────── §D · Loading states ────────────────────────────────

struct HomeViewLoading: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Garasi Saya")
                Skeleton.Card(lines: 2, height: 160)
                    .padding(.bottom, 16)
                VStack(spacing: 0) {
                    ForEach(0..<3, id: \.self) { _ in
                        Skeleton.Row(leading: .icon, lines: 2)
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
            }
        }
        .background(Color.sgBgWarm)
    }
}

struct HistoryViewLoading: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Riwayat Servis")
                Skeleton.Tile(count: 4, columns: 4)
                    .frame(height: 36)
                    .padding(.bottom, 14)
                VStack(spacing: 8) {
                    ForEach(0..<5, id: \.self) { _ in
                        Skeleton.Row(leading: .icon, lines: 2)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                            .padding(.horizontal, 16)
                    }
                }
            }
        }
        .background(Color.sgBgWarm)
    }
}

struct RemindersViewLoading: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Pengingat Servis")
                Skeleton.Tile(count: 4, columns: 4)
                    .frame(height: 36)
                    .padding(.bottom, 14)
                VStack(spacing: 8) {
                    ForEach(0..<3, id: \.self) { _ in
                        Skeleton.Row(leading: .icon, lines: 2)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                            .padding(.horizontal, 16)
                    }
                }
                Spacer().frame(height: 12)
                VStack(spacing: 8) {
                    ForEach(0..<3, id: \.self) { _ in
                        Skeleton.Row(leading: .icon, lines: 2)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                            .padding(.horizontal, 16)
                    }
                }
            }
        }
        .background(Color.sgBgWarm)
    }
}

struct ComponentDetailLoading: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 12) {
                Skeleton.Card(lines: 3, height: 120)
                    .padding(.top, 16)
                ForEach(0..<3, id: \.self) { _ in
                    Skeleton.Row(leading: .icon, lines: 2)
                        .background(Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                        .overlay(
                            RoundedRectangle(cornerRadius: 18)
                                .strokeBorder(Color.sgBorder, lineWidth: 1)
                        )
                        .padding(.horizontal, 16)
                }
            }
        }
        .background(Color.sgBgWarm)
    }
}

#Preview("Home empty") {
    HomeViewEmpty()
}

#Preview("Home loading") {
    HomeViewLoading()
}
