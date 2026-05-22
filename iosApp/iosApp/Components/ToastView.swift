import SwiftUI

enum ToastTone {
    case neutral, success, warning, danger

    var bg: Color {
        switch self {
        case .neutral: return Color(red: 0.13, green: 0.16, blue: 0.20)
        case .success: return .sgPrimary
        case .warning: return .sgWarning
        case .danger:  return .sgDanger
        }
    }

    var fg: Color { .white }

    var iconUnicode: String {
        switch self {
        case .neutral: return "\u{f05a}"
        case .success: return "\u{f00c}"
        case .warning: return "\u{f071}"
        case .danger:  return "\u{f00d}"
        }
    }
}

struct ToastMessage: Equatable {
    let id: UUID
    let message: String
    let tone: ToastTone
    let undoLabel: String?

    init(message: String, tone: ToastTone = .neutral, undoLabel: String? = nil) {
        self.id = UUID()
        self.message = message
        self.tone = tone
        self.undoLabel = undoLabel
    }
}

struct ToastView: View {
    let toast: ToastMessage
    var onUndo: (() -> Void)? = nil

    var body: some View {
        HStack(spacing: 10) {
            Text(toast.tone.iconUnicode)
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(toast.tone.fg)

            Text(toast.message)
                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                .foregroundColor(toast.tone.fg)
                .lineLimit(2)
                .frame(maxWidth: .infinity, alignment: .leading)

            if let label = toast.undoLabel, let onUndo {
                Button(action: onUndo) {
                    Text(label.uppercased())
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .kerning(0.6)
                        .foregroundColor(toast.tone.fg)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.white.opacity(0.18))
                        .clipShape(Capsule())
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(toast.tone.bg)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .shadow(color: Color.black.opacity(0.18), radius: 16, x: 0, y: 6)
    }
}

// MARK: - Toast modifier

private struct ToastModifier: ViewModifier {
    @Binding var toast: ToastMessage?
    var onUndo: (() -> Void)?
    var autoDismissAfter: TimeInterval = 3.0

    @State private var workItem: DispatchWorkItem?

    func body(content: Content) -> some View {
        content
            .overlay(alignment: .bottom) {
                if let t = toast {
                    ToastView(toast: t, onUndo: onUndo)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 24)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                        .onAppear { scheduleDismiss() }
                        .onTapGesture { dismiss() }
                }
            }
            .animation(.spring(response: 0.4, dampingFraction: 0.85), value: toast)
            .onChange(of: toast) { _ in scheduleDismiss() }
    }

    private func scheduleDismiss() {
        workItem?.cancel()
        guard toast != nil else { return }
        let item = DispatchWorkItem { dismiss() }
        workItem = item
        DispatchQueue.main.asyncAfter(deadline: .now() + autoDismissAfter, execute: item)
    }

    private func dismiss() {
        workItem?.cancel()
        toast = nil
    }
}

extension View {
    /// Overlay a toast at the bottom of the screen. Bind a `ToastMessage?` —
    /// set it to show, it auto-dismisses after `autoDismissAfter`.
    func toast(
        _ toast: Binding<ToastMessage?>,
        onUndo: (() -> Void)? = nil,
        autoDismissAfter: TimeInterval = 3.0
    ) -> some View {
        modifier(ToastModifier(toast: toast, onUndo: onUndo, autoDismissAfter: autoDismissAfter))
    }
}

#Preview {
    StatefulToast()
}

private struct StatefulToast: View {
    @State var toast: ToastMessage? = nil
    var body: some View {
        VStack(spacing: 12) {
            Button("Show success") {
                toast = .init(message: "Servis disimpan", tone: .success, undoLabel: "Urungkan")
            }
            Button("Show neutral") {
                toast = .init(message: "Catatan diperbarui", tone: .neutral)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
        .toast($toast, onUndo: {})
    }
}
