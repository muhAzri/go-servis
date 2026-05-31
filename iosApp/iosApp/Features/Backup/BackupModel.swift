import Foundation
import UIKit
import Shared

@MainActor
final class BackupModel: ObservableObject {
    static let shared = BackupModel()

    @Published private(set) var counts: BackupCounts

    let vm: BackupViewModel
    private var cancellable: Cancellable?

    private init() {
        vm = PresentationFactory.shared.backupViewModel()
        counts = (vm.state.value as! BackupViewModel.UiState).counts
        cancellable = vm.observeState { [weak self] newState in
            DispatchQueue.main.async { self?.counts = newState.counts }
        }
    }

    func export(sections: Set<String>, range: String) async throws -> BackupExport {
        try await vm.export(sectionKeys: sections, rangeKey: range)
    }

    func importCsv(_ content: String) async throws -> BackupImportResult {
        try await vm.importCsv(content: content)
    }

    deinit { cancellable?.cancel() }
}

/// Writes the CSV to a temp file and returns its URL for sharing.
func writeBackupTempFile(_ export: BackupExport) -> URL? {
    let url = FileManager.default.temporaryDirectory.appendingPathComponent(export.filename)
    guard let data = export.content.data(using: .utf8) else { return nil }
    do {
        try data.write(to: url, options: .atomic)
        return url
    } catch {
        return nil
    }
}

/// Presents the system share sheet on top of the current UI.
func presentBackupShareSheet(items: [Any], onComplete: @escaping () -> Void = {}) {
    let activityVC = UIActivityViewController(activityItems: items, applicationActivities: nil)
    activityVC.completionWithItemsHandler = { _, _, _, _ in onComplete() }
    guard let scene = UIApplication.shared.connectedScenes
            .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
          let root = scene.windows.first(where: \.isKeyWindow)?.rootViewController
    else { return }
    var presenter = root
    while let presented = presenter.presentedViewController, !presented.isBeingDismissed {
        presenter = presented
    }
    if let pop = activityVC.popoverPresentationController {
        pop.sourceView = presenter.view
        pop.sourceRect = CGRect(x: presenter.view.bounds.midX, y: presenter.view.bounds.midY, width: 0, height: 0)
        pop.permittedArrowDirections = []
    }
    presenter.present(activityVC, animated: true)
}
