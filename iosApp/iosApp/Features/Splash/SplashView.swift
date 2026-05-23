import SwiftUI
import Shared

struct SplashView: View {
    let gate: any AppGate
    let onResolved: (any AppGate) -> Void

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
        .task(id: gateKey) {
            if gate is AppGateLoading { return }
            try? await Task.sleep(nanoseconds: 900_000_000)
            onResolved(gate)
        }
    }

    private var gateKey: String {
        switch gate {
        case is AppGateLoading: return "loading"
        case is AppGateMain: return "main"
        case let onb as AppGateOnboarding: return "onb-\(onb.resumeStep.name)"
        default: return "unknown"
        }
    }
}

#Preview {
    SplashView(gate: AppGateLoading.shared, onResolved: { _ in })
}
