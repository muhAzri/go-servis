import SwiftUI

/// Full-screen error state: 96pt circle (dangerSoft) + info icon + title + body + retry CTA.
struct ErrorStateView: View {
    let title: String
    let message: String
    var iconUnicode: String = "\u{f05a}"
    var retryLabel: String? = "Coba lagi"
    var onRetry: (() -> Void)? = nil
    var secondaryLabel: String? = nil
    var onSecondary: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: 0) {
            ZStack {
                Circle()
                    .fill(Color.sgDangerSoft)
                    .frame(width: 96, height: 96)
                Text(iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 40))
                    .foregroundColor(.sgDanger)
            }

            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)
                .multilineTextAlignment(.center)
                .padding(.top, 20)

            Text(message)
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextMuted)
                .multilineTextAlignment(.center)
                .lineSpacing(4)
                .frame(maxWidth: 280)
                .padding(.top, 8)

            if let retryLabel, let onRetry {
                Button(action: onRetry) {
                    Text(retryLabel)
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.white)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 14)
                        .frame(minWidth: 220)
                        .background(Color.sgPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                }
                .buttonStyle(.plain)
                .padding(.top, 24)
            }

            if let secondaryLabel, let onSecondary {
                Button(action: onSecondary) {
                    Text(secondaryLabel)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                        .foregroundColor(.sgTextMuted)
                        .underline()
                }
                .buttonStyle(.plain)
                .padding(.top, 12)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.horizontal, 24)
        .padding(.vertical, 32)
    }
}

#Preview {
    ErrorStateView(
        title: "Gagal memuat",
        message: "Terjadi kesalahan saat mengambil data. Cek koneksi lalu coba lagi.",
        onRetry: {}
    )
    .background(Color.sgBgWarm)
}
