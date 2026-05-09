import UIKit
import YandexMobileAds

@MainActor
final class AppOpenManager: NSObject {
    static let shared = AppOpenManager()

    private let loader = AppOpenAdLoader()
    private var ad: AppOpenAd?
    private var loadedAt: Date?
    private var lastShownAt: Date?
    private var hasBeenInBackground = false
    private var isPresentingAd = false
    private var allowed = true

    private let freshness: TimeInterval = 4 * 60 * 60
    private let cooldown: TimeInterval = 60

    private override init() {}

    func start() {
        NotificationCenter.default.addObserver(
            self, selector: #selector(onWillEnterForeground),
            name: UIApplication.willEnterForegroundNotification, object: nil
        )
        NotificationCenter.default.addObserver(
            self, selector: #selector(onDidEnterBackground),
            name: UIApplication.didEnterBackgroundNotification, object: nil
        )
        load()
    }

    func setAllowed(_ value: Bool) {
        allowed = value
    }

    private func load() {
        let request = AdRequest(adUnitID: AdsConfig.appOpenUnitId)
        loader.loadAd(with: request) { [weak self] result in
            guard let self else { return }
            switch result {
            case .success(let openAd):
                openAd.delegate = self
                self.ad = openAd
                self.loadedAt = Date()
            case .failure:
                break
            }
        }
    }

    @objc private func onDidEnterBackground() {
        hasBeenInBackground = true
    }

    @objc private func onWillEnterForeground() {
        guard allowed, hasBeenInBackground, !isPresentingAd else { return }
        if let last = lastShownAt, Date().timeIntervalSince(last) < cooldown { return }
        if let loaded = loadedAt, Date().timeIntervalSince(loaded) > freshness {
            ad = nil
            load()
            return
        }
        guard let openAd = ad, let presenter = topMostController() else { return }
        isPresentingAd = true
        openAd.show(from: presenter)
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

extension AppOpenManager: AppOpenAdDelegate {
    func appOpenAdDidShow(_ appOpenAd: AppOpenAd) {
        lastShownAt = Date()
    }
    func appOpenAdDidDismiss(_ appOpenAd: AppOpenAd) {
        ad = nil
        isPresentingAd = false
        load()
    }
    func appOpenAdDidClick(_ appOpenAd: AppOpenAd) {}
    func appOpenAd(_ appOpenAd: AppOpenAd, didFailToShow error: Error) {
        ad = nil
        isPresentingAd = false
        load()
    }
    func appOpenAd(_ appOpenAd: AppOpenAd, didTrackImpression impressionData: ImpressionData?) {}
}
