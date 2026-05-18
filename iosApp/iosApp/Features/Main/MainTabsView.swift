import SwiftUI

struct MainTabsView: View {
    @Environment(AppRouter.self) private var router
    @State private var selectedTab: BottomTab = .home

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch selectedTab {
                case .home:
                    HomeView(
                        onOpenReminders: { selectedTab = .reminders },
                        onOpenReminderDetail: { router.navigate(to: .reminderDetail) },
                        onOpenVehicleDetail: { router.navigate(to: .vehicleDetail) },
                        onAddService: { router.navigate(to: .addService) },
                        onAddVehicle: { router.navigate(to: .addVehicle) },
                        onUpdateOdometer: { router.navigate(to: .updateOdometer) },
                        onOpenTips: { router.navigate(to: .tips) }
                    )

                case .reminders:
                    RemindersView(
                        onOpenReminderDetail: { router.navigate(to: .reminderDetail) }
                    )

                case .add:
                    HomeView()

                case .history:
                    HistoryView()

                case .settings:
                    SettingsView(
                        onOpenPrivacy: { router.navigate(to: .privacy) },
                        onOpenTerms: { router.navigate(to: .terms) },
                        onOpenAbout: { router.navigate(to: .about) },
                        onOpenHelp: { router.navigate(to: .help) },
                        onOpenTestScreen: { router.navigate(to: .test) }
                    )
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.sgBgWarm)

            BottomNavBar(
                selected: $selectedTab,
                onSelect: { tab in
                    if tab == .add { router.navigate(to: .addService) }
                }
            )
        }
        .background(Color.sgBgWarm)
        .ignoresSafeArea(edges: .bottom)
        .onAppear { AppOpenManager.shared.setAllowed(true) }
        .onDisappear { AppOpenManager.shared.setAllowed(false) }
    }
}
