import SwiftUI

struct AppNavGraph: View {
    @AppStorage("onboarding_done") private var onboardingDone = false
    @State private var router = AppRouter()
    @State private var isShowingSplash = true
    @State private var userName: String = ""
    @State private var userColorId: String = "primary"

    var body: some View {
        if isShowingSplash {
            SplashView {
                isShowingSplash = false
            }
        } else if !onboardingDone {
            OnboardingFlowView(
                onComplete: { typedName in
                    userName = typedName
                    onboardingDone = true
                }
            )
            .transition(.asymmetric(
                insertion: .move(edge: .trailing),
                removal: .move(edge: .leading)
            ))
        } else {
            NavigationStack(path: $router.path) {
                MainTabsView(userName: userName, userColorId: userColorId)
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
        case .main:
            MainTabsView(userName: userName, userColorId: userColorId)
        case .addVehicle:
            AddVehicleView(
                onBack: { router.navigateBack() },
                onSaved: { _ in router.navigateBack() }
            )
        case .test:
            TestView()
        case .privacy:
            PrivacyView(onBack: { router.navigateBack() })
        case .terms:
            TermsView(onBack: { router.navigateBack() })
        case .about:
            AboutView(onBack: { router.navigateBack() })
        case .help:
            HelpView(onBack: { router.navigateBack() })

        case .reminderDetail:
            ReminderDetailView(
                onMarkServiced: { router.navigate(to: .addService) }
            )
        case .addReminder:
            AddReminderView(
                onSaved: { router.navigateBack() }
            )
        case .addReminderFromContext:
            AddReminderView(
                onSaved: { router.navigateBack() },
                fromContext: true
            )
        case .addService:
            AddServiceView(
                onSaved: { router.navigate(to: .interstitialAd) }
            )
        case .interstitialAd:
            InterstitialAdView(
                onClose: { router.navigate(to: .serviceSaved) }
            )
        case .serviceSaved:
            ServiceSavedView(
                onBackToHome: { router.popToRoot() },
                onOpenHistory: { router.popToRoot() },
                onOpenServiceDetail: {
                    router.popToRoot()
                    router.navigate(to: .serviceDetail)
                },
                onAddReminderFromContext: {
                    router.popToRoot()
                    router.navigate(to: .addReminderFromContext)
                }
            )
        case .serviceDetail:
            ServiceDetailView(
                onEdit: { router.navigate(to: .addService) },
                onOpenNextReminder: { router.navigate(to: .reminderDetail) }
            )
        case .vehicleDetail:
            VehicleDetailView()
        case .updateOdometer:
            UpdateOdometerView(
                onSave: { router.navigateBack() }
            )
        case .tips:
            TipsView(
                onOpenTipDetail: { router.navigate(to: .tipsDetail) }
            )
        case .tipsDetail:
            TipsDetailView(
                onOpenAddService: { router.navigate(to: .addService) }
            )
        case .editProfile:
            EditProfileView(
                initialName: userName,
                initialColorId: userColorId,
                onSave: { name, colorId in
                    userName = name
                    userColorId = colorId
                    router.navigateBack()
                },
                onCancel: { router.navigateBack() }
            )

        case .vehicleComponents:
            VehicleComponentsView()
        case .componentDetail(let componentId):
            ComponentDetailView(
                componentId: componentId,
                onSave: { router.navigateBack() },
                onStopMonitoring: { router.navigateBack() }
            )
        case .addCustomComponent:
            AddCustomComponentView(
                onAdd: { _ in router.navigateBack() }
            )
        }
    }
}

struct OnboardingFlowView: View {
    let onComplete: (_ name: String) -> Void

    enum Step { case carousel, name, pickType, addVehicle(String), notifPerm }
    @State private var step: Step = .carousel
    @State private var selectedVehicleType: String = ""
    @State private var typedName: String = ""

    var body: some View {
        ZStack {
            switch step {
            case .carousel:
                OnboardingView(
                    onSkip: { onComplete("") },
                    onFinish: {
                        withAnimation(.easeInOut(duration: 0.3)) { step = .name }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .name:
                NameView(
                    onBack: {
                        withAnimation(.easeInOut(duration: 0.3)) { step = .carousel }
                    },
                    onNext: { name in
                        typedName = name
                        withAnimation(.easeInOut(duration: 0.3)) { step = .pickType }
                    },
                    onSkip: {
                        typedName = ""
                        withAnimation(.easeInOut(duration: 0.3)) { step = .pickType }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .pickType:
                PickVehicleTypeView(
                    onBack: {
                        withAnimation(.easeInOut(duration: 0.3)) { step = .name }
                    },
                    onPickType: { type in
                        selectedVehicleType = type
                        withAnimation(.easeInOut(duration: 0.3)) { step = .addVehicle(type) }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .addVehicle(let type):
                OnboardingAddVehicleView(
                    vehicleType: type,
                    onBack: {
                        withAnimation(.easeInOut(duration: 0.3)) { step = .pickType }
                    },
                    onComplete: { _ in
                        withAnimation(.easeInOut(duration: 0.3)) { step = .notifPerm }
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .notifPerm:
                NotifPermissionView(
                    onBack: {
                        withAnimation(.easeInOut(duration: 0.3)) { step = .addVehicle(selectedVehicleType) }
                    },
                    onComplete: { onComplete(typedName) }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
            }
        }
    }
}
