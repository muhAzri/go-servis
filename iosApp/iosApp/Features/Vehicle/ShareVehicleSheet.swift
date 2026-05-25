import SwiftUI
import Shared
import UIKit

struct ShareVehicleSheet: View {
    let vehicle: Vehicle?
    var onDismiss: () -> Void = {}
    var onCopied: () -> Void = {}

    private var displayName: String {
        let name = vehicle?.displayTitle.trimmingCharacters(in: .whitespaces) ?? ""
        return name.isEmpty ? "kendaraan" : name
    }

    private var summaryPreview: String {
        guard let v = vehicle else { return "" }
        let km: String = {
            let n = Int(truncating: v.odometer as NSNumber)
            let f = NumberFormatter()
            f.numberStyle = .decimal
            f.groupingSeparator = "."
            return f.string(from: NSNumber(value: n)) ?? String(n)
        }()
        return "\"\(displayName) · \(km) km · …\""
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Bagikan \(displayName)")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                Text("Pilih cara berbagi ringkasan kendaraan ini.")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            .padding(.horizontal, 20)
            .padding(.top, 18)
            .padding(.bottom, 14)

            Divider().background(Color.sgBorder)

            ScrollView {
                VStack(spacing: 0) {
                    row(icon: "\u{f15c}", label: "Salin ringkasan teks", sub: summaryPreview, action: copy)
                    Divider().background(Color.sgBorder).padding(.leading, 70)
                    row(icon: "\u{f1e0}", label: "Bagikan via aplikasi lain", sub: "WhatsApp, email, dll.", action: systemShare)
                }
            }

            Button(action: onDismiss) {
                Text("Batal")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .frame(maxWidth: .infinity)
                    .frame(height: 46)
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 16)
            .padding(.top, 8)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
    }

    private func copy() {
        guard let v = vehicle else { onDismiss(); return }
        let text = PresentationFactory.shared.vehicleShareText(vehicle: v)
        UIPasteboard.general.string = text
        onDismiss()
        onCopied()
    }

    private func systemShare() {
        guard let v = vehicle else { onDismiss(); return }
        let text = PresentationFactory.shared.vehicleShareText(vehicle: v)
        // Present the activity sheet on top of our sheet, then dismiss ours when it's done.
        presentActivitySheet(items: [text]) { onDismiss() }
    }

    private func row(icon: String, label: String, sub: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 14) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.sgSurfaceAlt)
                        .frame(width: 40, height: 40)
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 18))
                        .foregroundColor(.sgTextPrimary)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(label)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(sub)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .lineLimit(1)
                }
                Spacer()
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 14)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private func presentActivitySheet(items: [Any], onComplete: @escaping () -> Void = {}) {
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
    // iPad needs a popover anchor; use the presenter's view center.
    if let pop = activityVC.popoverPresentationController {
        pop.sourceView = presenter.view
        pop.sourceRect = CGRect(x: presenter.view.bounds.midX, y: presenter.view.bounds.midY, width: 0, height: 0)
        pop.permittedArrowDirections = []
    }
    presenter.present(activityVC, animated: true)
}
