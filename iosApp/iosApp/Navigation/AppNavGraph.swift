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

        case .reminderDetail(let reminderId):
            ReminderDetailView(
                reminderId: reminderId,
                onMarkServiced: { reminderId, vehicleId in
                    router.navigate(to: .addService(vehicleId: vehicleId.isEmpty ? nil : vehicleId, sourceReminderId: reminderId))
                },
                onEdit: { id in router.navigate(to: .editReminder(reminderId: id)) },
                onDeleted: { router.navigateBack() }
            )
        case .addReminder(let vehicleId, let trackedComponentId):
            AddReminderView(
                onSaved: { router.navigateBack() },
                vehicleId: vehicleId,
                trackedComponentId: trackedComponentId
            )
        case .addReminderFromContext(let fromContext, let vehicleId):
            AddReminderView(
                onSaved: { router.navigateBack() },
                vehicleId: vehicleId,
                fromContext: fromContext
            )
        case .addService(let vehicleId, let sourceReminderId, let trackedComponentId):
            AddServiceView(
                onSaved: { recordId in router.navigate(to: .interstitialAd(recordId: recordId)) },
                vehicleId: vehicleId,
                sourceReminderId: sourceReminderId,
                trackedComponentId: trackedComponentId
            )
        case .editService(let recordId):
            EditServiceView(
                recordId: recordId,
                onSaved: { router.navigateBack() },
                onDeleted: { router.popToRoot() }
            )
        case .interstitialAd(let recordId):
            InterstitialAdView(
                onClose: { router.navigate(to: .serviceSaved(recordId: recordId)) }
            )
        case .serviceSaved(let recordId):
            ServiceSavedView(
                onBackToHome: { router.popToRoot() },
                onOpenHistory: { router.popToRoot() },
                onOpenServiceDetail: {
                    if let id = recordId {
                        router.popToRoot()
                        router.navigate(to: .serviceDetail(recordId: id))
                    } else {
                        router.popToRoot()
                    }
                },
                onAddReminderFromContext: {
                    router.popToRoot()
                    router.navigate(to: .addReminderFromContext())
                },
                recordId: recordId
            )
        case .serviceDetail(let recordId):
            ServiceDetailView(
                recordId: recordId,
                onEdit: { router.navigate(to: .editService(recordId: recordId)) },
                onOpenNextReminder: { /* TBD */ }
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
                onOpenAddService: { router.navigate(to: .addService()) }
            )
        case .editProfile:
            EditProfileView(
                onBack: { router.navigateBack() },
                onSaved: { router.navigateBack() }
            )

        case .vehicleComponents(let vehicleId):
            VehicleComponentsView(
                vehicleId: vehicleId,
                onOpenTracked: { trackedId in
                    router.navigate(to: .trackedComponentDetail(trackedId: trackedId))
                },
                onOpenCatalog: { catalogId in
                    router.navigate(to: .componentInfo(vehicleId: vehicleId, catalogId: catalogId))
                },
                onAdd: { router.navigate(to: .addCustomComponent(vehicleId: vehicleId)) }
            )
        case .componentInfo(let vehicleId, let catalogId, let customName):
            ComponentInfoView(
                vehicleId: vehicleId,
                catalogId: catalogId,
                customName: customName,
                onTracked: { _ in
                    // Setelah dipantau, kembali ke daftar tambah komponen.
                    router.navigateBack()
                }
            )
        case .trackedComponentDetail(let trackedId):
            TrackedComponentDetailView(
                trackedId: trackedId,
                onStopped: { router.navigateBack() },
                onLogServiceForComponent: {
                    router.navigate(to: .addService(trackedComponentId: trackedId))
                },
                onCreateReminderForComponent: {
                    router.navigate(to: .addReminder(trackedComponentId: trackedId))
                }
            )
        case .addCustomComponent(let vehicleId):
            AddCustomComponentView(
                vehicleId: vehicleId,
                onOpenComponent: { catalogId in
                    router.navigate(to: .componentInfo(vehicleId: vehicleId, catalogId: catalogId))
                },
                onCreateCustom: { name in
                    router.navigate(to: .componentInfo(vehicleId: vehicleId, customName: name))
                }
            )

        case .editVehicle(let vehicleId):
            EditVehicleView(
                vehicleId: vehicleId,
                onSaved: { router.navigateBack() },
                onDeleted: { router.popToRoot() }
            )
        case .editReminder(let reminderId):
            EditReminderView(
                reminderId: reminderId,
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
