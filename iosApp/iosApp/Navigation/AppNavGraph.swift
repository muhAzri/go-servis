import SwiftUI

struct AppNavGraph: View {
    @AppStorage("onboarding_done") private var onboardingDone = false
    @State private var router = AppRouter()
    @State private var isShowingSplash = true

    var body: some View {
        if isShowingSplash {
            SplashView {
                isShowingSplash = false
            }
        } else if !onboardingDone {
            OnboardingFlowView {
                onboardingDone = true
            }
            .transition(.asymmetric(
                insertion: .move(edge: .trailing),
                removal: .move(edge: .leading)
            ))
        } else {
            NavigationStack(path: $router.path) {
                MainTabsView()
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
            MainTabsView()
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
                onOpenHistory: { router.popToRoot() }
            )
        case .vehicleDetail:
            VehicleDetailView()
        case .updateOdometer:
            UpdateOdometerView(
                onSave: { router.navigateBack() }
            )
        case .tips:
            TipsView()
        }
    }
}

struct OnboardingFlowView: View {
    let onComplete: () -> Void

    enum Step { case carousel, pickType, addVehicle(String), notifPerm }
    @State private var step: Step = .carousel
    @State private var selectedVehicleType: String = ""

    var body: some View {
        ZStack {
            switch step {
            case .carousel:
                OnboardingView(
                    onSkip: onComplete,
                    onFinish: {
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
                        withAnimation(.easeInOut(duration: 0.3)) { step = .carousel }
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
                    onComplete: onComplete
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
            }
        }
    }
}
