import UserNotifications
import Shared

/// Owns the notification category (with the inline snooze action) and handles
/// foreground presentation, taps (deep-link), and the snooze action.
final class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
    static let shared = NotificationDelegate()

    private let categoryId = "SERVICE_REMINDER"
    private let snoozeActionId = "SNOOZE"

    func register() {
        let center = UNUserNotificationCenter.current()
        center.delegate = self

        let snooze = UNNotificationAction(
            identifier: snoozeActionId,
            title: "Tunda 3 hari",
            options: []
        )
        let category = UNNotificationCategory(
            identifier: categoryId,
            actions: [snooze],
            intentIdentifiers: [],
            options: []
        )
        center.setNotificationCategories([category])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .list, .sound])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let reminderId = response.notification.request.content.userInfo["reminderId"] as? String

        if response.actionIdentifier == snoozeActionId, let id = reminderId {
            NotificationActions.shared.snooze(reminderId: id, days: 3)
        } else if response.actionIdentifier == UNNotificationDefaultActionIdentifier, let id = reminderId {
            Task { @MainActor in NotificationRouter.shared.open(id) }
        }
        completionHandler()
    }
}
