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
    case addService(vehicleId: String? = nil, sourceReminderId: String? = nil, trackedComponentId: String? = nil)
    case editService(recordId: String)
    case interstitialAd(recordId: String? = nil)
    case serviceSaved(recordId: String? = nil)
    case serviceDetail(recordId: String)
    case vehicleDetail(vehicleId: String? = nil)
    case updateOdometer(vehicleId: String? = nil)
    case tips
    case tipsDetail
    case editProfile

    // Component management
    case vehicleComponents(vehicleId: String)
    case componentInfo(vehicleId: String, catalogId: String? = nil, customName: String? = nil)
    case trackedComponentDetail(trackedId: String)
    case addCustomComponent(vehicleId: String)

    // New plot-hole screens
    case editVehicle(vehicleId: String? = nil)
    case editReminder
    case vehicleList
}
