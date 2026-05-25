import SwiftUI
import Shared

struct AppNavGraph: View {
    @StateObject private var gateModel = AppGateModel()
    @StateObject private var onboardingModel = OnboardingFlowModel()
    @State private var router = AppRouter()
    @State private var splashResolved = false
    @State private var resolvedGate: any AppGate = AppGateLoading.shared

    var body: some View {
        Group {
            if !splashResolved {
                SplashView(gate: gateModel.gate) { gate in
                    resolvedGate = gate
                    splashResolved = true
                }
            } else if resolvedGate is AppGateOnboarding {
                let resumeStep = (resolvedGate as? AppGateOnboarding)?.resumeStep ?? OnboardingStep.welcome
                OnboardingFlowView(
                    flow: onboardingModel,
                    initialStep: resumeStep,
                    onComplete: { resolvedGate = AppGateMain.shared }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
            } else {
                NavigationStack(path: $router.path) {
                    MainTabsView(
                        userName: onboardingModel.state.persistedName ?? ""
                    )
                    .navigationDestination(for: AppDestination.self) { destination in
                        destinationView(for: destination)
                    }
                }
                .environment(router)
            }
        }
        .onReceive(gateModel.$gate) { newGate in
            if splashResolved, newGate is AppGateMain {
                resolvedGate = AppGateMain.shared
            }
        }
    }

    @ViewBuilder
    private func destinationView(for destination: AppDestination) -> some View {
        switch destination {
        case .main:
            MainTabsView(
                userName: onboardingModel.state.persistedName ?? ""
            )
        case .addVehicle:
            AddVehicleView(
                onSaved: { router.navigateBack() }
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
                onMarkServiced: { router.navigate(to: .addService) },
                onEdit: { router.navigate(to: .editReminder) },
                onDelete: { router.navigateBack() }
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
                onDelete: { router.navigateBack() },
                onOpenNextReminder: { router.navigate(to: .reminderDetail) }
            )
        case .vehicleDetail(let vehicleId):
            VehicleDetailView(
                vehicleId: vehicleId,
                onEdit: { id in router.navigate(to: .editVehicle(vehicleId: id)) }
            )
        case .updateOdometer(let vehicleId):
            UpdateOdometerView(
                vehicleId: vehicleId,
                onSaved: { router.navigateBack() }
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
                onBack: { router.navigateBack() },
                onSaved: { router.navigateBack() }
            )

        case .vehicleComponents:
            VehicleComponentsView()
        case .componentDetail(let componentId):
            ComponentDetailView(
                componentId: componentId,
                onSave: { router.navigateBack() },
                onStopMonitoring: { router.navigateBack() },
                onLogServiceForComponent: { router.navigate(to: .addService) },
                onCreateReminderForComponent: { router.navigate(to: .addReminder) }
            )
        case .addCustomComponent:
            AddCustomComponentView(
                onAdd: { _ in router.navigateBack() }
            )

        case .editVehicle(let vehicleId):
            EditVehicleView(
                vehicleId: vehicleId,
                onSaved: { router.navigateBack() },
                onDeleted: { router.popToRoot() }
            )
        case .editReminder:
            EditReminderView(
                onSave: { router.navigateBack() },
                onDelete: { router.popToRoot() }
            )
        case .vehicleList:
            VehicleListView(
                onBack: { router.navigateBack() },
                onOpenVehicle: { id in router.navigate(to: .vehicleDetail(vehicleId: id)) },
                onAddVehicle: { router.navigate(to: .addVehicle) }
            )
        }
    }
}

private enum FlowStep: Equatable {
    case carousel, name, pickType, addVehicle(String), notifPerm

    static func from(_ step: OnboardingStep, lastType: String) -> FlowStep {
        switch step {
        case OnboardingStep.welcome: return .carousel
        case OnboardingStep.profilename: return .name
        case OnboardingStep.pickvehicletype: return .pickType
        case OnboardingStep.addvehicle: return .addVehicle(lastType)
        case OnboardingStep.notificationpermission: return .notifPerm
        case OnboardingStep.done: return .carousel
        default: return .carousel
        }
    }
}

struct OnboardingFlowView: View {
    @ObservedObject var flow: OnboardingFlowModel
    let initialStep: OnboardingStep
    let onComplete: () -> Void

    @State private var step: FlowStep = .carousel
    @State private var selectedVehicleType: String = "motor"

    var body: some View {
        ZStack {
            switch step {
            case .carousel:
                OnboardingView(
                    onSkip: { flow.skipAll() },
                    onFinish: { flow.finishCarousel() }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .name:
                NameView(
                    flow: flow,
                    onBack: { withAnimation(.easeInOut(duration: 0.3)) { step = .carousel } },
                    onNext: { flow.submitName() },
                    onSkip: { flow.skipName() }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .pickType:
                PickVehicleTypeView(
                    onBack: { withAnimation(.easeInOut(duration: 0.3)) { step = .name } },
                    onPickType: { type in
                        selectedVehicleType = type
                        flow.pickVehicleType(type)
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .addVehicle(let type):
                OnboardingAddVehicleView(
                    vehicleType: type,
                    onBack: { withAnimation(.easeInOut(duration: 0.3)) { step = .pickType } },
                    onSkip: { flow.skipVehicle() },
                    onComplete: { state in
                        flow.submitVehicle(state.toOnboardingInput(typeKey: type))
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))

            case .notifPerm:
                NotifPermissionView(
                    onBack: { withAnimation(.easeInOut(duration: 0.3)) { step = .addVehicle(selectedVehicleType) } },
                    onComplete: { granted in
                        flow.recordNotificationPermission(asked: true, granted: granted)
                    }
                )
                .transition(.asymmetric(
                    insertion: .move(edge: .trailing),
                    removal: .move(edge: .leading)
                ))
            }
        }
        .onAppear {
            selectedVehicleType = flow.state.pickedVehicleType?.key ?? selectedVehicleType
            step = FlowStep.from(initialStep, lastType: selectedVehicleType)
            flow.onEvent = { event in
                handle(event: event)
            }
        }
        .onDisappear { flow.onEvent = nil }
    }

    private func handle(event: any OnboardingEvent) {
        switch event {
        case let go as OnboardingEventGoTo:
            withAnimation(.easeInOut(duration: 0.3)) {
                let lastType = flow.state.pickedVehicleType?.key ?? selectedVehicleType
                selectedVehicleType = lastType
                step = FlowStep.from(go.step, lastType: lastType)
            }
        case is OnboardingEventCompletedFlow:
            onComplete()
        default:
            break
        }
    }
}
