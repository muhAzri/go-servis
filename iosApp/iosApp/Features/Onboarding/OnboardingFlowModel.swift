import Foundation
import Shared

@MainActor
final class OnboardingFlowModel: ObservableObject {
    @Published private(set) var state: OnboardingUiState = OnboardingUiState(
        step: OnboardingStep.welcome,
        persistedName: nil,
        nameInput: "",
        pickedVehicleType: nil,
        firstVehicleId: nil,
        isSubmitting: false,
        error: nil
    )

    let viewModel: OnboardingFlowViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onEvent: ((any OnboardingEvent) -> Void)?

    init() {
        viewModel = PresentationFactory.shared.onboardingFlowViewModel()
        stateCancellable = viewModel.observeState { [weak self] value in
            DispatchQueue.main.async { self?.state = value }
        }
        eventCancellable = viewModel.observeEvents { [weak self] event in
            DispatchQueue.main.async { self?.onEvent?(event) }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func setNameInput(_ value: String) { viewModel.setNameInput(value: value) }
    func finishCarousel() { viewModel.finishCarousel() }
    func submitName() { viewModel.submitName() }
    func skipName() { viewModel.skipName() }
    func pickVehicleType(_ typeKey: String) {
        viewModel.pickVehicleType(type: VehicleType.companion.fromKey(key: typeKey))
    }
    func submitVehicle(_ input: OnboardingVehicleInput) { viewModel.submitVehicle(input: input) }
    func skipVehicle() { viewModel.skipVehicle() }
    func recordNotificationPermission(asked: Bool, granted: Bool) {
        viewModel.recordNotificationPermission(asked: asked, granted: granted)
    }
    func skipAll() { viewModel.skipAll() }
    func goBack(to step: OnboardingStep) { viewModel.goBack(to: step) }
}
