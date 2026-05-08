import SwiftUI

struct AppNavGraph: View {
    @State private var router = AppRouter()
    @State private var isShowingSplash = true

    var body: some View {
        if isShowingSplash {
            SplashView {
                isShowingSplash = false
            }
        } else {
            NavigationStack(path: $router.path) {
                HomeView()
                    .navigationDestination(for: AppDestination.self) { destination in
                        destinationView(for: destination)
                    }
            }
            .environment(router)
        }
    }

    @ViewBuilder
    private func destinationView(for destination: AppDestination) -> some View {
        switch destination {
        case .home:
            HomeView()
        }
    }
}
