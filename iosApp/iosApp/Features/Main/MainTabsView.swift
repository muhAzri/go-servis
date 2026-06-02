import SwiftUI

struct MainTabsView: View {
    var userName: String = ""

    @Environment(AppRouter.self) private var router
    @ObservedObject private var vehicles = VehicleListModel.shared
    @State private var selectedTab: BottomTab = .home
    @State private var showNoVehiclePrompt: Bool = false

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch selectedTab {
                case .home:
                    HomeView(
                        userName: userName,
                        onOpenReminders: { selectedTab = .reminders },
                        onOpenReminderDetail: { reminderId in router.navigate(to: .reminderDetail(reminderId: reminderId)) },
                        onOpenVehicleDetail: { router.navigate(to: .vehicleDetail()) },
                        onOpenVehicleList: { router.navigate(to: .vehicleList) },
                        onAddService: { router.navigate(to: .addService()) },
                        onAddVehicle: { router.navigate(to: .addVehicle) },
                        onUpdateOdometer: { router.navigate(to: .updateOdometer()) }
                    )

                case .reminders:
                    RemindersView(
                        onOpenReminderDetail: { reminderId in router.navigate(to: .reminderDetail(reminderId: reminderId)) },
                        onAddReminder: { router.navigate(to: .addReminder()) }
                    )

                case .add:
                    HomeView(userName: userName)

                case .history:
                    HistoryView(
                        onOpenServiceDetail: { recordId in router.navigate(to: .serviceDetail(recordId: recordId)) },
                        onAddService: { router.navigate(to: .addService()) }
                    )

                case .settings:
                    SettingsView(
                        onOpenPrivacy: { router.navigate(to: .privacy) },
                        onOpenTerms: { router.navigate(to: .terms) },
                        onOpenAbout: { router.navigate(to: .about) },
                        onOpenHelp: { router.navigate(to: .help) },
                        onOpenEditProfile: { router.navigate(to: .editProfile) }
                    )
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.sgBgWarm)

            BottomNavBar(
                selected: $selectedTab,
                onSelect: { tab in
                    if tab == .add {
                        if vehicles.state.vehicles.isEmpty && !vehicles.state.isLoading {
                            showNoVehiclePrompt = true
                        } else {
                            router.navigate(to: .addService())
                        }
                    }
                }
            )
        }
        .background(Color.sgBgWarm)
        .ignoresSafeArea(edges: .bottom)
        .onAppear { AppOpenManager.shared.setAllowed(true) }
        .onDisappear { AppOpenManager.shared.setAllowed(false) }
        .alert("Tambah kendaraan dulu", isPresented: $showNoVehiclePrompt) {
            Button("Batal", role: .cancel) {}
            Button("Tambah Kendaraan") { router.navigate(to: .addVehicle) }
        } message: {
            Text("Belum ada kendaraan untuk dicatatkan servisnya. Tambah kendaraan dulu yuk?")
        }
    }
}
