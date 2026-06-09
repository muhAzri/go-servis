import UIKit

/// Public links + share action for the "Bagikan App" buttons.
enum AppLinks {
    static let landingPage = "https://www.goservis.my.id/"

    /// Presents the system share sheet with the marketing landing page link.
    static func shareApp() {
        let text = "Coba ServisGo — pengingat servis motor & mobil biar nggak telat ganti oli. "
            + "Info & download: \(landingPage)"
        var items: [Any] = [text]
        if let url = URL(string: landingPage) { items.append(url) }

        let activityVC = UIActivityViewController(activityItems: items, applicationActivities: nil)

        guard let scene = UIApplication.shared.connectedScenes
            .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
            let root = scene.keyWindow?.rootViewController
        else { return }

        var top = root
        while let presented = top.presentedViewController { top = presented }

        // Required so it doesn't crash on iPad (popover needs an anchor).
        if let popover = activityVC.popoverPresentationController {
            popover.sourceView = top.view
            popover.sourceRect = CGRect(
                x: top.view.bounds.midX,
                y: top.view.bounds.midY,
                width: 0,
                height: 0
            )
            popover.permittedArrowDirections = []
        }

        top.present(activityVC, animated: true)
    }
}
