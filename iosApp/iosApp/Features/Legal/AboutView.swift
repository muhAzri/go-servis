import SwiftUI

struct AboutView: View {
    let onBack: () -> Void

    var body: some View {
        LegalScreenShell(title: "Tentang ServisGo", onBack: onBack) {
            VStack(spacing: 0) {
                Spacer().frame(height: 20)

                ZStack {
                    RoundedRectangle(cornerRadius: 24, style: .continuous)
                        .fill(Color.sgPrimary)
                    Text("\u{f0ad}")
                        .font(.custom("FontAwesome6Free-Solid", size: 38))
                        .foregroundColor(.white)
                }
                .frame(width: 88, height: 88)
                .shadow(color: Color.sgPrimary.opacity(0.33), radius: 18, x: 0, y: 16)

                Text("ServisGo")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 24))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.4)
                    .padding(.top, 14)

                Text("v1.0.0 · build 2026.05.06")
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
                    .padding(.top, 2)

                Text("Pengingat servis sederhana untuk pemilik motor & mobil di Indonesia. Catat sekali, lupa-lupa nanti — kami yang ingatkan.")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextPrimary)
                    .multilineTextAlignment(.center)
                    .lineSpacing(6)
                    .padding(.top, 14)
            }
            .frame(maxWidth: .infinity)

            VStack(spacing: 0) {
                infoRow(label: "Pengembang", value: "Muhammad Azri Fatihah Susanto (Personal)")
                Rectangle().fill(Color.sgBorder).frame(height: 1)
                infoRow(label: "Email", value: "muhammad.azri.f.s@gmail.com")
            }
            .padding(.horizontal, 16)
            .background(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.sgSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
            .padding(.top, 16)

            HStack(spacing: 8) {
                outlinedButton(text: "★ Beri Rating")
                outlinedButton(text: "↗ Bagikan App")
            }
            .padding(.top, 14)
        }
    }

    private func infoRow(label: String, value: String) -> some View {
        HStack {
            Text(label)
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
            Spacer()
            Text(value)
                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                .foregroundColor(.sgTextPrimary)
                .multilineTextAlignment(.trailing)
        }
        .padding(.vertical, 12)
    }

    private func outlinedButton(text: String) -> some View {
        Text(text)
            .font(.custom("PlusJakartaSans-Bold", size: 13))
            .foregroundColor(.sgTextPrimary)
            .frame(maxWidth: .infinity)
            .frame(height: 44)
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.sgSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
    }
}

#Preview {
    AboutView(onBack: {})
}
