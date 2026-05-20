import Foundation

enum AppDestination: Hashable {
    case main
    case addVehicle
    case test
    case privacy
    case terms
    case about
    case help

    // Core flow
    case reminderDetail
    case addReminder
    case addReminderFromContext
    case addService
    case interstitialAd
    case serviceSaved
    case serviceDetail
    case vehicleDetail
    case updateOdometer
    case tips
    case tipsDetail
    case editProfile

    // Component management
    case vehicleComponents
    case componentDetail(componentId: String)
    case addCustomComponent
}
