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
        log("start() — registering observers and loading ad")
        NotificationCenter.default.addObserver(
            self, selector: #selector(onDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification, object: nil
        )
        NotificationCenter.default.addObserver(
            self, selector: #selector(onDidEnterBackground),
            name: UIApplication.didEnterBackgroundNotification, object: nil
        )
        load()
    }

    func setAllowed(_ value: Bool) {
        log("setAllowed(\(value))")
        allowed = value
    }

    private func load() {
        log("load() requesting ad for unitId=\(AdsConfig.appOpenUnitId)")
        let request = AdRequest(adUnitID: AdsConfig.appOpenUnitId)
        loader.loadAd(with: request) { [weak self] result in
            guard let self else { return }
            switch result {
            case .success(let openAd):
                openAd.delegate = self
                self.ad = openAd
                self.loadedAt = Date()
                self.log("load() success — ad cached")
            case .failure(let error):
                self.log("load() FAILED: \(error.localizedDescription)")
            }
        }
    }

    @objc private func onDidEnterBackground() {
        log("didEnterBackground — hasBeenInBackground=true")
        hasBeenInBackground = true
    }

    @objc private func onDidBecomeActive() {
        log("didBecomeActive — allowed=\(allowed) hasBeenInBackground=\(hasBeenInBackground) isPresentingAd=\(isPresentingAd) adReady=\(ad != nil)")
        guard allowed else { log("blocked: allowed=false"); return }
        guard hasBeenInBackground else { log("blocked: never been in background (cold start)"); return }
        guard !isPresentingAd else { log("blocked: already presenting"); return }
        if let last = lastShownAt, Date().timeIntervalSince(last) < cooldown {
            log("blocked: cooldown — \(Int(cooldown - Date().timeIntervalSince(last)))s remaining")
            return
        }
        if let loaded = loadedAt, Date().timeIntervalSince(loaded) > freshness {
            log("ad stale (>\(Int(freshness/3600))h) — discarding and reloading")
            ad = nil
            load()
            return
        }
        guard let openAd = ad else { log("blocked: no ad cached — reloading"); load(); return }
        guard let presenter = topMostController() else { log("blocked: no presenter (scene not foreground-active yet?)"); return }
        log("calling show(from: \(type(of: presenter)))")
        isPresentingAd = true
        openAd.show(from: presenter)
    }

    private func log(_ message: String) {
        #if DEBUG
        print("[AppOpen] \(message)")
        #endif
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
        log("delegate: didShow")
        lastShownAt = Date()
    }
    func appOpenAdDidDismiss(_ appOpenAd: AppOpenAd) {
        log("delegate: didDismiss — clearing & reloading")
        ad = nil
        isPresentingAd = false
        load()
    }
    func appOpenAdDidClick(_ appOpenAd: AppOpenAd) {
        log("delegate: didClick")
    }
    func appOpenAd(_ appOpenAd: AppOpenAd, didFailToShow error: Error) {
        log("delegate: didFailToShow — \(error.localizedDescription)")
        ad = nil
        isPresentingAd = false
        load()
    }
    func appOpenAd(_ appOpenAd: AppOpenAd, didTrackImpression impressionData: ImpressionData?) {
        log("delegate: didTrackImpression")
    }
}
