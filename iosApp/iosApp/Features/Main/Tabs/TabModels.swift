import Foundation
import Shared

@MainActor
final class VehicleListModel: ObservableObject {
    static let shared = VehicleListModel()

    @Published private(set) var state: VehicleListViewModel.UiState

    let vm: VehicleListViewModel
    private var cancellable: Cancellable?

    private init() {
        vm = PresentationFactory.shared.vehicleListViewModel()
        state = vm.state.value as! VehicleListViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }
}

@MainActor
final class ReminderListModel: ObservableObject {
    static let shared = ReminderListModel()

    @Published private(set) var state: ReminderListViewModel.UiState

    let vm: ReminderListViewModel
    private var cancellable: Cancellable?

    private init() {
        vm = PresentationFactory.shared.reminderListViewModel()
        state = vm.state.value as! ReminderListViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }
}

@MainActor
final class ServiceHistoryModel: ObservableObject {
    static let shared = ServiceHistoryModel()

    @Published private(set) var state: ServiceHistoryViewModel.UiState

    let vm: ServiceHistoryViewModel
    private var cancellable: Cancellable?

    private init() {
        vm = PresentationFactory.shared.serviceHistoryViewModel()
        state = vm.state.value as! ServiceHistoryViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }
}

@MainActor
final class ProfileModel: ObservableObject {
    static let shared = ProfileModel()

    @Published private(set) var state: ProfileViewModel.UiState

    let vm: ProfileViewModel
    private var cancellable: Cancellable?

    private init() {
        vm = PresentationFactory.shared.profileViewModel()
        state = vm.state.value as! ProfileViewModel.UiState
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
    }
}
