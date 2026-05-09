import SwiftUI

struct MainTabsView: View {
    @Environment(AppRouter.self) private var router
    @State private var selectedTab: BottomTab = .beranda

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch selectedTab {
                case .beranda:   GarasiView()
                case .pengingat: PengingatView()
                case .add:       GarasiView()
                case .riwayat:   RiwayatView()
                case .saya:
                    PengaturanView(onOpenTestScreen: {
                        router.navigate(to: .test)
                    })
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.sgBgWarm)

            BottomNavBar(
                selected: $selectedTab,
                onSelect: { tab in
                    if tab == .add { router.navigate(to: .addVehicle) }
                }
            )
        }
        .background(Color.sgBgWarm)
        .ignoresSafeArea(edges: .bottom)
        .onAppear { AppOpenManager.shared.setAllowed(true) }
        .onDisappear { AppOpenManager.shared.setAllowed(false) }
    }
}
