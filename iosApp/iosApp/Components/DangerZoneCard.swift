import SwiftUI

/// "ZONA BAHAYA" section: divider + danger-outline button + optional helper body.
struct DangerZoneCard: View {
    let buttonLabel: String
    let onTap: () -> Void
    var bodyText: String? = nil
    var iconUnicode: String = "\u{f1f8}"
    var sectionTitle: String = "Zona Bahaya"

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 10) {
                Rectangle()
                    .fill(Color.sgDanger.opacity(0.35))
                    .frame(height: 1)
                    .frame(maxWidth: .infinity)
                Text(sectionTitle.uppercased())
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .kerning(0.8)
                    .foregroundColor(.sgDanger)
                    .fixedSize()
                Rectangle()
                    .fill(Color.sgDanger.opacity(0.35))
                    .frame(height: 1)
                    .frame(maxWidth: .infinity)
            }

            if let bodyText, !bodyText.isEmpty {
                Text(bodyText)
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(3)
                    .fixedSize(horizontal: false, vertical: true)
            }

            Button(action: onTap) {
                HStack(spacing: 8) {
                    Text(iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                    Text(buttonLabel)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                }
                .foregroundColor(.sgDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(Color.sgDangerSoft.opacity(0.4))
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgDanger.opacity(0.45), lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
        }
    }
}

#Preview {
    DangerZoneCard(
        buttonLabel: "Hapus servis",
        onTap: {},
        bodyText: "Riwayat servis akan dihapus permanen dan tidak bisa dikembalikan."
    )
    .padding()
    .background(Color.sgBgWarm)
}
