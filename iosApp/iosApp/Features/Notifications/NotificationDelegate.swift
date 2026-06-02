import UserNotifications
import Shared

/// Owns the notification category (with the inline snooze action) and handles
/// foreground presentation, taps (deep-link), and the snooze action.
final class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
    static let shared = NotificationDelegate()

    private let categoryId = "SERVICE_REMINDER"
    private let odometerCategoryId = "ODOMETER_REMINDER"
    private let snoozeActionId = "SNOOZE"

    func register() {
        let center = UNUserNotificationCenter.current()
        center.delegate = self

        let snooze = UNNotificationAction(
            identifier: snoozeActionId,
            title: "Tunda 3 hari",
            options: []
        )
        let serviceCategory = UNNotificationCategory(
            identifier: categoryId,
            actions: [snooze],
            intentIdentifiers: [],
            options: []
        )
        let odometerCategory = UNNotificationCategory(
            identifier: odometerCategoryId,
            actions: [],
            intentIdentifiers: [],
            options: []
        )
        center.setNotificationCategories([serviceCategory, odometerCategory])
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
        let userInfo = response.notification.request.content.userInfo
        let reminderId = userInfo["reminderId"] as? String
        let odometerVehicleId = userInfo["vehicleId"] as? String

        if response.actionIdentifier == snoozeActionId, let id = reminderId {
            NotificationActions.shared.snooze(reminderId: id, days: 3)
        } else if response.actionIdentifier == UNNotificationDefaultActionIdentifier {
            if let id = reminderId {
                Task { @MainActor in NotificationRouter.shared.open(id) }
            } else if let vId = odometerVehicleId {
                Task { @MainActor in NotificationRouter.shared.openOdometer(vId) }
            }
        }
        completionHandler()
    }
}
