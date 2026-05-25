import Foundation
import Shared

@MainActor
final class AddVehicleModel: ObservableObject {
    @Published private(set) var state: AddVehicleViewModel.UiState

    let vm: AddVehicleViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.addVehicleViewModel()
        state = vm.state.value as! AddVehicleViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is AddVehicleViewModelEventSaved {
                    self?.onSaved?()
                } else if let failed = event as? AddVehicleViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func submit(input: OnboardingVehicleInput) { vm.submit(input: input) }
}

@MainActor
final class EditVehicleModel: ObservableObject {
    @Published private(set) var state: EditVehicleViewModel.UiState

    let vm: EditVehicleViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onDeleted: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.editVehicleViewModel()
        state = vm.state.value as! EditVehicleViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is EditVehicleViewModelEventSaved {
                    self?.onSaved?()
                } else if event is EditVehicleViewModelEventDeleted {
                    self?.onDeleted?()
                } else if let failed = event as? EditVehicleViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(vehicleId: String?) { vm.load(vehicleId: vehicleId) }
    func save(input: OnboardingVehicleInput) { vm.save(input: input) }
    func delete() { vm.delete() }
}

@MainActor
final class UpdateOdometerModel: ObservableObject {
    @Published private(set) var state: UpdateOdometerViewModel.UiState

    let vm: UpdateOdometerViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.updateOdometerViewModel()
        state = vm.state.value as! UpdateOdometerViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is UpdateOdometerViewModelEventSaved {
                    self?.onSaved?()
                } else if let failed = event as? UpdateOdometerViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func preselect(vehicleId: String?) { vm.preselect(vehicleId: vehicleId) }
    func select(vehicleId: String) { vm.select(vehicleId: vehicleId) }
    func setOdometer(km: Int64?) {
        if let km = km {
            vm.setOdometer(km: KotlinLong(value: km))
        } else {
            vm.setOdometer(km: nil)
        }
    }
    func save(allowRollback: Bool = false) { vm.save(allowRollback: allowRollback) }
}

@MainActor
final class VehicleDetailModel: ObservableObject {
    @Published private(set) var state: VehicleDetailViewModel.UiState

    let vm: VehicleDetailViewModel
    private var cancellable: Cancellable?

    init() {
        vm = PresentationFactory.shared.vehicleDetailViewModel()
        state = vm.state.value as! VehicleDetailViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }

    deinit {
        cancellable?.cancel()
    }

    func load(vehicleId: String?) { vm.load(vehicleId: vehicleId) }
}

extension VehicleFormState {
    init(vehicle: Vehicle) {
        let resolvedSubtype: String = {
            let raw = vehicle.subtypeId
            if raw.isEmpty || raw == "*" {
                return VehicleSubtypes.defaultFor(vehicleType: vehicle.type.key)
            }
            return raw
        }()
        self.init(
            type: vehicle.type.key,
            subtype: resolvedSubtype,
            nama: vehicle.nickname,
            merek: vehicle.brand,
            model: vehicle.model,
            tahun: vehicle.year.map { String(describing: $0) } ?? "",
            platNomor: vehicle.plateNumber,
            odometer: String(vehicle.odometer),
            warna: PresentationFactory.shared.vehicleColorHex(vehicle: vehicle)
        )
    }
}
