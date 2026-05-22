import SwiftUI
import YandexMobileAds

struct AdsNativeCardView: UIViewRepresentable {
    var unitId: String = AdsConfig.nativeContentUnitId
    @Binding var state: AdLoadState

    func makeCoordinator() -> Coordinator { Coordinator(state: $state) }

    func makeUIView(context: Context) -> AdsNativeAdView {
        let view = AdsNativeAdView()
        view.isHidden = true
        context.coordinator.load(into: view, unitId: unitId)
        return view
    }

    func updateUIView(_ uiView: AdsNativeAdView, context: Context) {}

    static func dismantleUIView(_ uiView: AdsNativeAdView, coordinator: Coordinator) {
        coordinator.tearDown()
    }

    @MainActor
    final class Coordinator: NSObject, NativeAdDelegate {
        private let loader = NativeAdLoader()
        private var ad: NativeAd?
        private let state: Binding<AdLoadState>

        init(state: Binding<AdLoadState>) {
            self.state = state
        }

        func load(into view: AdsNativeAdView, unitId: String) {
            let request = AdRequest(adUnitID: unitId)
            loader.loadAd(with: request, options: NativeAdOptions()) { [weak self, weak view] result in
                guard let self, let view else { return }
                switch result {
                case .success(let nativeAd):
                    nativeAd.delegate = self
                    do {
                        try nativeAd.bind(with: view)
                        view.isHidden = false
                        self.ad = nativeAd
                        self.state.wrappedValue = .loaded
                    } catch {
                        view.isHidden = true
                        self.state.wrappedValue = .failed
                    }
                case .failure:
                    self.state.wrappedValue = .failed
                }
            }
        }

        func tearDown() {
            ad?.delegate = nil
            ad = nil
        }

        func nativeAdDidClick(_ ad: NativeAd) {}
        func nativeAd(_ ad: NativeAd, didTrackImpression impressionData: ImpressionData?) {}
    }
}
