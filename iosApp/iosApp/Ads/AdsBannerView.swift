import SwiftUI
import YandexMobileAds

struct AdsBannerView: View {
    var unitId: String = AdsConfig.bannerUnitId
    var maxHeight: CGFloat = 250
    @Binding var state: AdLoadState

    var body: some View {
        GeometryReader { proxy in
            BannerAdRepresentable(
                unitId: unitId,
                width: proxy.size.width,
                maxHeight: maxHeight,
                state: $state
            )
        }
        .frame(height: state == .loaded ? maxHeight : 0)
    }
}

private struct BannerAdRepresentable: UIViewRepresentable {
    let unitId: String
    let width: CGFloat
    let maxHeight: CGFloat
    @Binding var state: AdLoadState

    func makeCoordinator() -> Coordinator { Coordinator(state: $state) }

    func makeUIView(context: Context) -> BannerAdView {
        let size = BannerAdSize.inline(width: max(width, 50), maxHeight: maxHeight)
        let view = BannerAdView(adSize: size)
        view.delegate = context.coordinator
        view.loadAd(with: AdRequest(adUnitID: unitId))
        return view
    }

    func updateUIView(_ uiView: BannerAdView, context: Context) {}

    static func dismantleUIView(_ uiView: BannerAdView, coordinator: Coordinator) {
        uiView.delegate = nil
        uiView.removeFromSuperview()
    }

    @MainActor
    final class Coordinator: NSObject, BannerAdViewDelegate {
        let state: Binding<AdLoadState>

        init(state: Binding<AdLoadState>) {
            self.state = state
        }

        func bannerAdViewDidLoad(_ bannerAdView: BannerAdView) {
            state.wrappedValue = .loaded
        }

        func bannerAdViewDidFailLoading(_ bannerAdView: BannerAdView, error: Error) {
            state.wrappedValue = .failed
        }

        func bannerAdViewDidClick(_ bannerAdView: BannerAdView) {}

        func bannerAdView(_ bannerAdView: BannerAdView, didTrackImpression impressionData: ImpressionData?) {}
    }
}
