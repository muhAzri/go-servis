import SwiftUI

struct ShareVehicleSheet: View {
    let vehicleName: String
    var onDismiss: () -> Void = {}
    var onCopy: () -> Void = {}
    var onExportImage: () -> Void = {}
    var onSystemShare: () -> Void = {}

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Bagikan \(vehicleName)")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                Text("Pilih cara berbagi data kendaraan ini.")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            .padding(.horizontal, 20)
            .padding(.top, 18)
            .padding(.bottom, 14)

            Divider().background(Color.sgBorder)

            ScrollView {
                VStack(spacing: 0) {
                    row(icon: "\u{f15c}", label: "Salin ringkasan teks", sub: "\"\(vehicleName) · 18.420 km · …\"", action: onCopy)
                    Divider().background(Color.sgBorder).padding(.leading, 70)
                    row(icon: "\u{f03e}", label: "Ekspor sebagai gambar (PNG)", sub: "Kartu kendaraan untuk dibagikan", action: onExportImage)
                    Divider().background(Color.sgBorder).padding(.leading, 70)
                    row(icon: "\u{f1e0}", label: "Bagikan via aplikasi lain", sub: "WhatsApp, email, dll.", action: onSystemShare)
                }
            }

            Button(action: onDismiss) {
                Text("Batal")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .frame(maxWidth: .infinity)
                    .frame(height: 46)
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 16)
            .padding(.top, 8)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
    }

    private func row(icon: String, label: String, sub: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 14) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.sgSurfaceAlt)
                        .frame(width: 40, height: 40)
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 18))
                        .foregroundColor(.sgTextPrimary)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(label)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(sub)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .lineLimit(1)
                }
                Spacer()
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}
