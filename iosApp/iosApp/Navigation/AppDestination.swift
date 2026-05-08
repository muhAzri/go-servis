import Foundation

/// All navigable destinations inside the main app (post-splash).
/// Add cases here as the app grows — each case can carry associated values for
/// passing typed data between screens (e.g. `.vehicleDetail(id: String)`).
enum AppDestination: Hashable {
    case home
}
