import SwiftUI

struct MainTabsView: View {
    var userName: String = ""
    var userColorId: String = "primary"

    @Environment(AppRouter.self) private var router
    @State private var selectedTab: BottomTab = .home

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch selectedTab {
                case .home:
                    HomeView(
                        userName: userName,
                        onOpenReminders: { selectedTab = .reminders },
                        onOpenReminderDetail: { router.navigate(to: .reminderDetail) },
                        onOpenVehicleDetail: { router.navigate(to: .vehicleDetail) },
                        onOpenVehicleList: { router.navigate(to: .vehicleList) },
                        onAddService: { router.navigate(to: .addService) },
                        onAddVehicle: { router.navigate(to: .addVehicle) },
                        onUpdateOdometer: { router.navigate(to: .updateOdometer) },
                        onOpenTips: { router.navigate(to: .tips) }
                    )

                case .reminders:
                    RemindersView(
                        onOpenReminderDetail: { router.navigate(to: .reminderDetail) },
                        onAddReminder: { router.navigate(to: .addReminder) }
                    )

                case .add:
                    HomeView(userName: userName)

                case .history:
                    HistoryView(
                        onOpenServiceDetail: { router.navigate(to: .serviceDetail) }
                    )

                case .settings:
                    SettingsView(
                        userName: userName,
                        userColorId: userColorId,
                        onOpenPrivacy: { router.navigate(to: .privacy) },
                        onOpenTerms: { router.navigate(to: .terms) },
                        onOpenAbout: { router.navigate(to: .about) },
                        onOpenHelp: { router.navigate(to: .help) },
                        onOpenTestScreen: { router.navigate(to: .test) },
                        onOpenEditProfile: { router.navigate(to: .editProfile) }
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
