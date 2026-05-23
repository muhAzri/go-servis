import Foundation
import Shared

@MainActor
final class AppGateModel: ObservableObject {
    @Published private(set) var gate: any AppGate = AppGateLoading.shared

    private let viewModel: AppGateViewModel
    private var cancellable: Cancellable?

    init() {
        viewModel = PresentationFactory.shared.appGateViewModel()
        cancellable = viewModel.observeGate { [weak self] value in
            DispatchQueue.main.async { self?.gate = value }
        }
    }

    deinit {
        cancellable?.cancel()
    }
}
