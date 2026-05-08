import SwiftUI

/// Central navigation state for the app. Inject via `.environment(router)` and
/// read with `@Environment(AppRouter.self)` in any child view.
@Observable
final class AppRouter {
    var path = NavigationPath()

    func navigate(to destination: AppDestination) {
        path.append(destination)
    }

    func navigateBack() {
        guard !path.isEmpty else { return }
        path.removeLast()
    }

    func popToRoot() {
        path.removeLast(path.count)
    }
}
