import Foundation
import Shared

@MainActor
final class VehicleComponentsModel: ObservableObject {
    @Published private(set) var state: VehicleComponentsViewModel.UiState

    let vm: VehicleComponentsViewModel
    private var cancellable: Cancellable?

    init() {
        vm = PresentationFactory.shared.vehicleComponentsViewModel()
        state = vm.state.value as! VehicleComponentsViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }

    deinit {
        cancellable?.cancel()
    }

    func load(vehicleId: String) { vm.load(vehicleId: vehicleId) }
}

@MainActor
final class ComponentInfoModel: ObservableObject {
    @Published private(set) var state: ComponentInfoViewModel.UiState

    let vm: ComponentInfoViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onTracked: ((String) -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.componentInfoViewModel()
        state = vm.state.value as! ComponentInfoViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if let tracked = event as? ComponentInfoViewModelEventTracked {
                    self?.onTracked?(tracked.trackedId)
                } else if let failed = event as? ComponentInfoViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(catalogId: String, vehicleId: String) {
        vm.load(catalogId: catalogId, vehicleId: vehicleId)
    }

    func loadNewCustom(name: String, vehicleId: String) {
        vm.loadNewCustom(name: name, vehicleId: vehicleId)
    }

    func setMode(_ mode: ComponentInfoViewModel.IntervalMode) { vm.setMode(mode: mode) }
    func setCustomKm(_ km: Int64?) {
        vm.setCustomKm(km: km.map { KotlinLong(value: $0) })
    }
    func setCustomDays(_ days: Int32?) {
        vm.setCustomDays(days: days.map { KotlinInt(value: $0) })
    }
    func track() { vm.track() }
}

@MainActor
final class TrackedComponentDetailModel: ObservableObject {
    @Published private(set) var state: TrackedComponentDetailViewModel.UiState

    let vm: TrackedComponentDetailViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onStopped: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.trackedComponentDetailViewModel()
        state = vm.state.value as! TrackedComponentDetailViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is TrackedComponentDetailViewModelEventSaved {
                    self?.onSaved?()
                } else if event is TrackedComponentDetailViewModelEventStopped {
                    self?.onStopped?()
                } else if let failed = event as? TrackedComponentDetailViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(trackedId: String) { vm.load(trackedId: trackedId) }
    func setMode(_ mode: TrackedComponentDetailViewModel.IntervalMode) { vm.setMode(mode: mode) }
    func setCustomKm(_ km: Int64?) {
        if let km = km {
            vm.setCustomKm(km: KotlinLong(value: km))
        } else {
            vm.setCustomKm(km: nil)
        }
    }
    func setCustomDays(_ days: Int32?) {
        if let days = days {
            vm.setCustomDays(days: KotlinInt(value: days))
        } else {
            vm.setCustomDays(days: nil)
        }
    }
    func save() { vm.save() }
    func stopMonitoring() { vm.stopMonitoring() }
}

@MainActor
final class AddTrackedComponentModel: ObservableObject {
    @Published private(set) var state: AddTrackedComponentViewModel.UiState

    let vm: AddTrackedComponentViewModel
    private var stateCancellable: Cancellable?

    init() {
        vm = PresentationFactory.shared.addTrackedComponentViewModel()
        state = vm.state.value as! AddTrackedComponentViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }

    deinit {
        stateCancellable?.cancel()
    }

    func load(vehicleId: String) { vm.load(vehicleId: vehicleId) }
    func setQuery(_ q: String) { vm.setQuery(query: q) }
}
