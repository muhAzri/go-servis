import Foundation

/// Bridges a tapped reminder notification into in-app navigation.
@MainActor
final class NotificationRouter: ObservableObject {
    static let shared = NotificationRouter()

    @Published var pendingReminderId: String?

    private init() {}

    func open(_ reminderId: String) {
        pendingReminderId = reminderId
    }

    func consume() {
        pendingReminderId = nil
    }
}
