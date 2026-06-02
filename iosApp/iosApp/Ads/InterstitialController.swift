import SwiftUI
import YandexMobileAds

/// Shows a Yandex interstitial at task-completion points (service saved, reminder
/// completed/added, vehicle added). All call sites share one frequency policy:
/// the user's first qualifying action is ad-free, then at most one ad per
/// `cooldown`. If no ad is ready the flow proceeds immediately — the user is
/// never blocked waiting for fill. Mirrors the Android `InterstitialAdManager`.
@MainActor
@Observable
final class InterstitialController: NSObject {
    static let shared = InterstitialController()

    private let loader = InterstitialAdLoader()
    private var ad: InterstitialAd?
    private(set) var isReady: Bool = false

    private var loadedAt: Date = .distantPast
    private var lastShownAt: Date = .distantPast
    private var qualifyingActions: Int = 0
    private var pendingOnFinished: (() -> Void)?

    private static let cooldown: TimeInterval = 3 * 60        // 3 minutes between ads
    private static let freshness: TimeInterval = 4 * 60 * 60  // cached ad considered stale after 4h
    private static let skipFirstActions = 1                   // first qualifying action is ad-free

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
                self.loadedAt = Date()
            case .failure:
                self.isReady = false
            }
        }
    }

    /// Run `onFinished` after an interstitial is dismissed, or immediately when the
    /// frequency policy skips this one or no ad is ready. `onFinished` always runs
    /// exactly once so the caller's navigation never stalls.
    func maybeShow(onFinished: @escaping () -> Void) {
        qualifyingActions += 1

        let now = Date()
        let isFirstAction = qualifyingActions <= Self.skipFirstActions
        let withinCooldown = now.timeIntervalSince(lastShownAt) < Self.cooldown
        let stale = now.timeIntervalSince(loadedAt) > Self.freshness

        guard !isFirstAction, !withinCooldown, !stale,
              let ad, let presenter = topMostController()
        else {
            if ad == nil || stale { clearAndReload() } // keep one warm for next time
            onFinished()
            return
        }

        pendingOnFinished = onFinished
        ad.show(from: presenter)
    }

    private func finishOnce() {
        let callback = pendingOnFinished
        pendingOnFinished = nil
        callback?()
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
    func interstitialAdDidShow(_ interstitialAd: InterstitialAd) { lastShownAt = Date() }
    func interstitialAdDidDismiss(_ interstitialAd: InterstitialAd) {
        clearAndReload()
        finishOnce()
    }
    func interstitialAdDidClick(_ interstitialAd: InterstitialAd) {}
    func interstitialAd(_ interstitialAd: InterstitialAd, didFailToShow error: Error) {
        clearAndReload()
        finishOnce()
    }
    func interstitialAd(_ interstitialAd: InterstitialAd, didTrackImpression impressionData: ImpressionData?) {}
}
