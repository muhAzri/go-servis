import SwiftUI
import YandexMobileAds

@MainActor
@Observable
final class InterstitialController: NSObject {
    static let shared = InterstitialController()

    private let loader = InterstitialAdLoader()
    private var ad: InterstitialAd?
    private(set) var isReady: Bool = false

    private override init() {
        super.init()
        load()
    }

    func load() {
        let request = AdRequest(adUnitID: AdsConfig.interstitialUnitId)
        loader.loadAd(with: request) { [weak self] result in
            guard let self else { return }
            switch result {
            case .success(let interstitial):
                interstitial.delegate = self
                self.ad = interstitial
                self.isReady = true
            case .failure:
                self.isReady = false
            }
        }
    }

    func show() {
        guard let ad, let presenter = topMostController() else { return }
        ad.show(from: presenter)
    }

    private func clearAndReload() {
        ad?.delegate = nil
        ad = nil
        isReady = false
        load()
    }

    private func topMostController() -> UIViewController? {
        guard let scene = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .first(where: { $0.activationState == .foregroundActive }),
              let root = scene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        else { return nil }
        var top = root
        while let presented = top.presentedViewController { top = presented }
        return top
    }
}

extension InterstitialController: InterstitialAdDelegate {
    func interstitialAdDidShow(_ interstitialAd: InterstitialAd) {}
    func interstitialAdDidDismiss(_ interstitialAd: InterstitialAd) { clearAndReload() }
    func interstitialAdDidClick(_ interstitialAd: InterstitialAd) {}
    func interstitialAd(_ interstitialAd: InterstitialAd, didFailToShow error: Error) { clearAndReload() }
    func interstitialAd(_ interstitialAd: InterstitialAd, didTrackImpression impressionData: ImpressionData?) {}
}
