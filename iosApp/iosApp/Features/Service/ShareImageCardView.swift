import SwiftUI

struct ShareImageCardView: View {
    var onBack: () -> Void = {}
    var onSave: () -> Void = {}
    var onShare: () -> Void = {}

    @State private var format: String = "image"
    @State private var sizeChoice: String = "portrait"
    @State private var includes: [String: Bool] = [
        "vehicle": true, "cost": true, "workshop": true, "note": false,
    ]

    var body: some View {
        VStack(spacing: 0) {
            DetailToolbar(
                onBack: onBack,
                title: "Bagikan Servis"
            )

            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    ShareCardPoster(
                        serviceLabel: "Ganti Oli Mesin",
                        serviceIconUnicode: "\u{f613}",
                        vehicleName: "Beat Hitam",
                        vehiclePlate: "B 4521 KZA",
                        vehicleIconUnicode: "\u{f21c}",
                        meta: [
                            .init(label: "Tanggal", value: "20 Feb 2026"),
                            .init(label: "KM", value: "16.000 km"),
                            .init(label: "Biaya", value: "Rp 65.000"),
                            .init(label: "Bengkel", value: "AHASS Kebon Jeruk"),
                        ]
                    )
                    .padding(.horizontal, 32)
                    .padding(.top, 12)

                    section("Format") {
                        HStack(spacing: 4) {
                            ForEach([("image", "Gambar (PNG)"), ("text", "Teks")], id: \.0) { opt in
                                Button(action: { format = opt.0 }) {
                                    Text(opt.1)
                                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                                        .foregroundColor(format == opt.0 ? .sgTextPrimary : .sgTextMuted)
                                        .frame(maxWidth: .infinity)
                                        .padding(.vertical, 10)
                                        .background(format == opt.0 ? Color.sgSurface : Color.clear)
                                        .clipShape(RoundedRectangle(cornerRadius: 9))
                                }
                                .buttonStyle(.plain)
                            }
                        }
                        .padding(4)
                        .background(Color.sgSurfaceAlt)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    }

                    section("Ukuran") {
                        HStack(spacing: 8) {
                            ForEach([
                                ("square", "1:1", "1080×1080", "◼"),
                                ("portrait", "4:5", "1080×1350", "▮"),
                                ("story", "9:16", "1080×1920", "▯"),
                            ], id: \.0) { opt in
                                let active = sizeChoice == opt.0
                                Button(action: { sizeChoice = opt.0 }) {
                                    VStack(spacing: 4) {
                                        Text(opt.3)
                                            .font(.system(size: 18))
                                            .foregroundColor(active ? .sgPrimary : .sgTextMuted)
                                        Text(opt.1)
                                            .font(.custom("PlusJakartaSans-ExtraBold", size: 12))
                                            .foregroundColor(active ? .sgPrimary : .sgTextPrimary)
                                        Text(opt.2)
                                            .font(.system(size: 9, weight: .regular, design: .monospaced))
                                            .foregroundColor(.sgTextSubtle)
                                    }
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(active ? Color.sgPrimarySoft : Color.sgSurface)
                                    .clipShape(RoundedRectangle(cornerRadius: 14))
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 14)
                                            .strokeBorder(active ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
                                    )
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }

                    section("Sertakan") {
                        VStack(spacing: 0) {
                            ForEach([
                                ("vehicle", "Nama & plat kendaraan"),
                                ("cost", "Biaya servis"),
                                ("workshop", "Nama bengkel"),
                                ("note", "Catatan teknis"),
                            ], id: \.0) { opt in
                                HStack {
                                    Text(opt.1)
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                                        .foregroundColor(.sgTextPrimary)
                                    Spacer()
                                    Toggle("", isOn: Binding(
                                        get: { includes[opt.0] ?? false },
                                        set: { includes[opt.0] = $0 }
                                    ))
                                    .labelsHidden()
                                    .tint(.sgPrimary)
                                }
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                if opt.0 != "note" {
                                    Divider().background(Color.sgBorder).padding(.horizontal, 14)
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

                    ContextBanner(
                        title: "Cocok untuk warranty claim",
                        body: "Bagikan ke grup dealer atau simpan sebagai bukti servis. ID #S0001 unik per record.",
                        iconUnicode: "\u{f05a}",
                        tone: .info
                    )
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }

            HStack(spacing: 10) {
                Button(action: onSave) {
                    HStack(spacing: 8) {
                        Text("\u{f15c}")
                            .font(.custom("FontAwesome6Free-Solid", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Text("Simpan")
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                    }
                    .padding(.horizontal, 18)
                    .frame(height: 50)
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                    )
                }
                .buttonStyle(.plain)

                Button(action: onShare) {
                    HStack(spacing: 8) {
                        Text("\u{f1e0}")
                            .font(.custom("FontAwesome6Free-Solid", size: 14))
                            .foregroundColor(.white)
                        Text("Bagikan Gambar")
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.sgPrimary)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(
                Color.sgSurface
                    .overlay(alignment: .top) {
                        Rectangle().fill(Color.sgBorder).frame(height: 1)
                    }
            )
        }
        .background(Color.sgBgWarm)
    }

    @ViewBuilder
    private func section<Content: View>(_ label: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
            content()
        }
    }
}
