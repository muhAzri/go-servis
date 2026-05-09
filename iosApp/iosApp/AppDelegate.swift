import UIKit
import YandexMobileAds

final class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        #if DEBUG
        YandexAds.enableLogging()
        #endif

        YandexAds.setUserConsent(true)
        YandexAds.setLocationTracking(false)
        YandexAds.setAgeRestricted(false)

        YandexAds.initializeSDK {
            AppOpenManager.shared.start()
        }
        return true
    }
}
