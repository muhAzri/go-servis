import Foundation
import Shared

@MainActor
final class AddServiceModel: ObservableObject {
    @Published private(set) var state: AddServiceViewModel.UiState

    let vm: AddServiceViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: ((String) -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.addServiceViewModel()
        state = vm.state.value as! AddServiceViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if let saved = event as? AddServiceViewModelEventSaved {
                    self?.onSaved?(saved.recordId)
                } else if let failed = event as? AddServiceViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func preselect(vehicleId: String?, sourceReminderId: String?, trackedComponentId: String?) {
        vm.preselect(vehicleId: vehicleId, sourceReminderId: sourceReminderId, trackedComponentId: trackedComponentId)
    }
    func selectVehicle(_ id: String) { vm.selectVehicle(vehicleId: id) }
    func setMode(_ mode: ServiceKind) { vm.setMode(mode: mode) }
    func setCustomTitle(_ value: String) { vm.setCustomTitle(value: value) }
    func setServiceDate(millis: Int64) { vm.setServiceDate(millis: millis) }
    func setOdometer(km: Int64?) {
        if let km = km { vm.setOdometer(km: KotlinLong(value: km)) } else { vm.setOdometer(km: nil) }
    }
    func setWorkshop(_ value: String) { vm.setWorkshop(value: value) }
    func setCost(_ idr: Int64) { vm.setCost(amountIdr: idr) }
    func setNote(_ value: String) { vm.setNote(value: value) }
    func toggleComponent(_ id: String) { vm.toggleComponent(componentId: id) }
    func setComponents(_ ids: Set<String>) { vm.setComponents(ids: ids) }
    func submit() { vm.submit() }
}

@MainActor
final class EditServiceModel: ObservableObject {
    @Published private(set) var state: EditServiceViewModel.UiState

    let vm: EditServiceViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onDeleted: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.editServiceViewModel()
        state = vm.state.value as! EditServiceViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is EditServiceViewModelEventSaved {
                    self?.onSaved?()
                } else if event is EditServiceViewModelEventDeleted {
                    self?.onDeleted?()
                } else if let failed = event as? EditServiceViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(recordId: String) { vm.load(recordId: recordId) }
    func selectVehicle(_ id: String) { vm.selectVehicle(vehicleId: id) }
    func setMode(_ mode: ServiceKind) { vm.setMode(mode: mode) }
    func setCustomTitle(_ value: String) { vm.setCustomTitle(value: value) }
    func setServiceDate(millis: Int64) { vm.setServiceDate(millis: millis) }
    func setOdometer(km: Int64?) {
        if let km = km { vm.setOdometer(km: KotlinLong(value: km)) } else { vm.setOdometer(km: nil) }
    }
    func setWorkshop(_ value: String) { vm.setWorkshop(value: value) }
    func setCost(_ idr: Int64) { vm.setCost(amountIdr: idr) }
    func setNote(_ value: String) { vm.setNote(value: value) }
    func toggleComponent(_ id: String) { vm.toggleComponent(componentId: id) }
    func setComponents(_ ids: Set<String>) { vm.setComponents(ids: ids) }
    func save() { vm.save() }
    func delete() { vm.delete() }
}

@MainActor
final class ServiceDetailModel: ObservableObject {
    @Published private(set) var state: ServiceDetailViewModel.UiState

    let vm: ServiceDetailViewModel
    private var stateCancellable: Cancellable?

    init() {
        vm = PresentationFactory.shared.serviceDetailViewModel()
        state = vm.state.value as! ServiceDetailViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }

    deinit { stateCancellable?.cancel() }

    func load(recordId: String) { vm.load(recordId: recordId) }
}
