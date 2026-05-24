import Foundation
import Shared

@MainActor
final class EditProfileModel: ObservableObject {
    @Published private(set) var state: EditProfileViewModel.UiState

    let vm: EditProfileViewModel
    private var stateCancellable: Cancellable?
    private var eventCancellable: Cancellable?
    var onSaved: (() -> Void)?
    var onFailed: ((DomainError) -> Void)?

    init() {
        vm = PresentationFactory.shared.editProfileViewModel()
        state = vm.state.value as! EditProfileViewModel.UiState
        stateCancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.state = newState }
        }
        eventCancellable = vm.observeEvents { [weak self] event in
            DispatchQueue.main.async {
                if event is EditProfileViewModelEventSaved {
                    self?.onSaved?()
                } else if let failed = event as? EditProfileViewModelEventFailed {
                    self?.onFailed?(failed.error)
                }
            }
        }
    }

    deinit {
        stateCancellable?.cancel()
        eventCancellable?.cancel()
    }

    func setName(_ value: String) { vm.setName(value: value) }
    func setAvatarColor(_ hex: String) { vm.setAvatarColor(hex: hex) }
    func setEmail(_ value: String) { vm.setEmail(value: value) }
    func resetToDefaults() { vm.resetToDefaults() }
    func save() { vm.save() }
}
