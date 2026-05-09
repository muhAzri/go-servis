import SwiftUI
import Shared

struct TestView: View {
    @Environment(AppRouter.self) private var router
    @State private var showContent = false
    @State private var showBanner = false
    @State private var showNative = false
    @State private var interstitial = InterstitialController.shared

    var body: some View {
        ScrollView {
            VStack(spacing: 8) {
                Text("Test Screen")
                    .font(.title2.bold())
                    .padding(.top, 8)

                Button("← Kembali") {
                    router.navigateBack()
                }
                Button("Click me!") {
                    withAnimation { showContent.toggle() }
                }
                Button("Tambah Kendaraan") {
                    router.navigate(to: .addVehicle)
                }

                Divider().padding(.vertical, 8)
                Text("— Yandex Ads test —").font(.subheadline)

                Button(showBanner ? "Hide inline banner" : "Test inline banner") {
                    showBanner.toggle()
                }
                Button(interstitial.isReady ? "Test interstitial" : "Loading interstitial…") {
                    interstitial.show()
                }
                .disabled(!interstitial.isReady)
                Button(showNative ? "Hide native ad" : "Test native ad") {
                    showNative.toggle()
                }

                if showBanner {
                    AdsBannerView()
                        .frame(maxWidth: .infinity)
                }
                if showNative {
                    AdsNativeCardView()
                        .frame(height: 360)
                }

                if showContent {
                    VStack(spacing: 16) {
                        Image(systemName: "swift")
                            .font(.system(size: 200))
                            .foregroundColor(.accentColor)
                        Text("SwiftUI: \(Greeting().greet())")
                    }
                    .transition(.move(edge: .top).combined(with: .opacity))
                }
            }
            .padding()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    }
}

#Preview {
    TestView()
}
