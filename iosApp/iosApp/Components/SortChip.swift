import SwiftUI

/// Pill chip used to trigger a sort sheet. Shows list icon + label + chevron-down.
struct SortChip: View {
    let label: String
    var iconUnicode: String = "\u{f0dc}" // sort
    var trailingUnicode: String = "\u{f078}" // chevron-down
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 6) {
                Text(iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 11))
                    .foregroundColor(.sgTextMuted)
                Text(label)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text(trailingUnicode)
                    .font(.custom("PlusJakartaSans-Bold", size: 9))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color.sgSurface)
            .clipShape(Capsule())
            .overlay(
                Capsule()
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    HStack {
        SortChip(label: "Terbaru", onTap: {})
        SortChip(label: "KM Tertinggi", onTap: {})
    }
    .padding()
    .background(Color.sgBgWarm)
}
