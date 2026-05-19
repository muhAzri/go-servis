import SwiftUI

struct SplashView: View {
    let onComplete: () -> Void
    
    var body: some View {
        ZStack {
            Color.sgPrimary

            VStack(spacing: 0) {
                ZStack {
                    RoundedRectangle(cornerRadius: 28)
                        .fill(Color.white)
                        .frame(width: 100, height: 100)
                        .shadow(color: .black.opacity(0.2), radius: 20, x: 0, y: 8)

                    Text("\u{f0ad}")
                        .font(.custom("FontAwesome6Free-Solid", size: 48))
                        .foregroundColor(.sgPrimary)
                }

                Spacer().frame(height: 24)

                Text("GoService")
                    .font(.custom("PlusJakartaSans-Bold", size: 38))
                    .foregroundColor(.white)
                    .kerning(38 * -0.03)

                Spacer().frame(height: 6)

                Text("Pengingat Servis Motor & Mobil")
                    .font(.custom("PlusJakartaSans-Regular", size: 14))
                    .foregroundColor(.white.opacity(0.85))
            }
        }
        .ignoresSafeArea()
        .task {
            try? await Task.sleep(nanoseconds: 1_500_000_000)
            onComplete()
        }
    }
}

#Preview {
    SplashView(onComplete: {})
}
