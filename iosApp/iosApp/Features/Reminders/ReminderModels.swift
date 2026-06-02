import Foundation
import Shared

@MainActor
final class AddReminderModel: ObservableObject {
    @Published private(set) var state: AddReminderViewModel.UiState

    let vm: AddReminderViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: ((String) -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.addReminderViewModel()
        state = vm.state.value as! AddReminderViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if let saved = event as? AddReminderViewModelEventSaved {
                    self?.onSaved?(saved.reminderId)
                } else if let failed = event as? AddReminderViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func preselect(vehicleId: String?, trackedComponentId: String?) {
        vm.preselect(vehicleId: vehicleId, trackedComponentId: trackedComponentId)
    }
    func selectVehicle(_ id: String) { vm.selectVehicle(vehicleId: id) }
    func setMode(_ mode: AddReminderViewModelMode) { vm.setMode(mode: mode) }
    func selectComponent(_ trackedId: String) { vm.selectComponent(trackedId: trackedId) }
    func setTitle(_ value: String) { vm.setTitle(value: value) }
    func setTriggerMode(_ mode: ReminderTriggerMode) { vm.setTriggerMode(mode: mode) }
    func setTargetKm(_ km: Int64?) {
        if let km = km { vm.setTargetKm(km: KotlinLong(value: km)) } else { vm.setTargetKm(km: nil) }
    }
    func setTargetDate(millis: Int64) { vm.setTargetDate(millis: millis) }
    func setNote(_ value: String) { vm.setNote(value: value) }
    func setNotifyDaysBefore(_ days: Int32) { vm.setNotifyDaysBefore(days: days) }
    func submit() { vm.submit() }
}

@MainActor
final class EditReminderModel: ObservableObject {
    @Published private(set) var state: EditReminderViewModel.UiState

    let vm: EditReminderViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onDeleted: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.editReminderViewModel()
        state = vm.state.value as! EditReminderViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is EditReminderViewModelEventSaved {
                    self?.onSaved?()
                } else if event is EditReminderViewModelEventDeleted {
                    self?.onDeleted?()
                } else if let failed = event as? EditReminderViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(reminderId: String) { vm.load(reminderId: reminderId) }
    func selectVehicle(_ id: String) { vm.selectVehicle(vehicleId: id) }
    func setServiceType(_ type: ServiceType) { vm.setServiceType(type: type) }
    func setTitle(_ value: String) { vm.setTitle(value: value) }
    func setTriggerMode(_ mode: ReminderTriggerMode) { vm.setTriggerMode(mode: mode) }
    func setTargetKm(_ km: Int64?) {
        if let km = km { vm.setTargetKm(km: KotlinLong(value: km)) } else { vm.setTargetKm(km: nil) }
    }
    func setTargetDate(millis: Int64) { vm.setTargetDate(millis: millis) }
    func setNote(_ value: String) { vm.setNote(value: value) }
    func setNotifyDaysBefore(_ days: Int32) { vm.setNotifyDaysBefore(days: days) }
    func save() { vm.save() }
    func delete() { vm.delete() }
}

@MainActor
final class ReminderDetailModel: ObservableObject {
    @Published private(set) var state: ReminderDetailViewModel.UiState

    let vm: ReminderDetailViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onCompleted: (() -> Void)?
    var onSnoozed: (() -> Void)?
    var onDismissed: (() -> Void)?
    var onDeleted: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.reminderDetailViewModel()
        state = vm.state.value as! ReminderDetailViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is ReminderDetailViewModelEventCompleted {
                    self?.onCompleted?()
                } else if event is ReminderDetailViewModelEventSnoozed {
                    self?.onSnoozed?()
                } else if event is ReminderDetailViewModelEventDismissed {
                    self?.onDismissed?()
                } else if event is ReminderDetailViewModelEventDeleted {
                    self?.onDeleted?()
                } else if let failed = event as? ReminderDetailViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func load(reminderId: String) { vm.load(reminderId: reminderId) }
    func complete(serviceRecordId: String? = nil) { vm.complete(serviceRecordId: serviceRecordId) }
    func snooze(_ duration: SnoozeReminder.SnoozeDuration) { vm.snooze(duration: duration) }
    func dismiss() { vm.dismiss() }
    func delete() { vm.delete() }
}
