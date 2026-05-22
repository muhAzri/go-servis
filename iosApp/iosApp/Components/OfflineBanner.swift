import SwiftUI

/// Full-width warning strip used when the app is offline or in a degraded state.
struct OfflineBanner: View {
    var title: String = "Sedang offline"
    var message: String = "Beberapa fitur mungkin tidak tersedia. Cek koneksi internetmu."
    var iconUnicode: String = "\u{f1ce}" // ban / no-wifi-ish
    var retryLabel: String? = nil
    var onRetry: (() -> Void)? = nil
    var onDismiss: (() -> Void)? = nil

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text(iconUnicode)
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(.sgWarning)
                .frame(width: 20)
                .padding(.top, 2)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text(message)
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(2)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            if let retryLabel, let onRetry {
                Button(action: onRetry) {
                    Text(retryLabel)
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(.sgWarning)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.7))
                        .clipShape(Capsule())
                        .overlay(
                            Capsule()
                                .strokeBorder(Color.sgWarning.opacity(0.4), lineWidth: 1)
                        )
                }
                .buttonStyle(.plain)
            }

            if let onDismiss {
                Button(action: onDismiss) {
                    Text("\u{f00d}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 24, height: 24)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .frame(maxWidth: .infinity)
        .background(Color.sgWarningSoft)
        .overlay(
            Rectangle()
                .fill(Color.sgWarning.opacity(0.3))
                .frame(height: 1),
            alignment: .bottom
        )
    }
}

#Preview {
    VStack(spacing: 0) {
        OfflineBanner(
            retryLabel: "Coba lagi",
            onRetry: {},
            onDismiss: {}
        )
        Spacer()
    }
    .background(Color.sgBgWarm)
}
