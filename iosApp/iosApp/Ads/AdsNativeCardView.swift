import SwiftUI
import YandexMobileAds

struct AdsNativeCardView: UIViewRepresentable {
    var unitId: String = AdsConfig.nativeContentUnitId

    func makeCoordinator() -> Coordinator { Coordinator() }

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

        func load(into view: AdsNativeAdView, unitId: String) {
            let request = AdRequest(adUnitID: unitId)
            loader.loadAd(with: request, options: NativeAdOptions()) { [weak self, weak view] result in
                guard let self, let view else { return }
                if case .success(let nativeAd) = result {
                    nativeAd.delegate = self
                    do {
                        try nativeAd.bind(with: view)
                        view.isHidden = false
                        self.ad = nativeAd
                    } catch {
                        view.isHidden = true
                    }
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
