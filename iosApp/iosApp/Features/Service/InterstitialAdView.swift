import SwiftUI

struct InterstitialAdView: View {
    let onClose: () -> Void

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [.sgPrimary, .sgPrimaryDark],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                AdTopBar(onClose: onClose)
                Spacer()
                AdContent()
                Spacer()
            }
        }
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

private struct AdTopBar: View {
    let onClose: () -> Void

    var body: some View {
        HStack {
            Text("IKLAN · 4s")
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(1)
                .foregroundColor(.white.opacity(0.7))
            Spacer()
            Button(action: onClose) {
                Text("\u{f00d}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.white)
                    .frame(width: 32, height: 32)
                    .background(Color.white.opacity(0.2))
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)
        }
        .padding(EdgeInsets(top: 14, leading: 16, bottom: 8, trailing: 16))
        .background(
            LinearGradient(
                colors: [Color.black.opacity(0.6), Color.clear],
                startPoint: .top,
                endPoint: .bottom
            )
        )
    }
}

private struct AdContent: View {
    var body: some View {
        VStack(spacing: 0) {
            Text("[ FULLSCREEN INTERSTITIAL · 320×480 ]")
                .font(.system(size: 10, design: .monospaced))
                .kerning(2)
                .foregroundColor(.white.opacity(0.7))

            ZStack {
                RoundedRectangle(cornerRadius: 28, style: .continuous)
                    .fill(Color.white)
                    .frame(width: 100, height: 100)
                Text("\u{f613}")
                    .font(.custom("FontAwesome6Free-Solid", size: 56))
                    .foregroundColor(.sgPrimary)
            }
            .padding(.vertical, 24)

            VStack(spacing: 0) {
                Text("Oli Federal Matic")
                Text("Diskon 25%")
            }
            .font(.custom("PlusJakartaSans-ExtraBold", size: 28))
            .foregroundColor(.white)
            .kerning(-0.5)
            .multilineTextAlignment(.center)
            .lineSpacing(2)

            Text("Khusus pengguna ServisGo — kode SERVIS25")
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.white.opacity(0.85))
                .padding(.top, 10)

            Button(action: {}) {
                HStack(spacing: 8) {
                    Text("Belanja Sekarang")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 15))
                    Text("\u{f061}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                }
                .foregroundColor(.sgPrimary)
                .padding(.horizontal, 40)
                .frame(height: 52)
                .background(Color.white)
                .clipShape(Capsule())
            }
            .buttonStyle(.plain)
            .padding(.top, 32)

            Text("Sponsored by Federal Oil")
                .font(.custom("PlusJakartaSans-Medium", size: 11))
                .foregroundColor(.white.opacity(0.7))
                .padding(.top, 14)
        }
        .padding(.horizontal, 32)
    }
}

#Preview {
    InterstitialAdView(onClose: {})
}
