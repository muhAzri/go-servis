import SwiftUI

// MARK: - Standard confirm (native alert wrapper)

struct ConfirmDialogConfig {
    let title: String
    let message: String
    var confirmLabel: String = "Konfirmasi"
    var cancelLabel: String = "Batal"
    var isDanger: Bool = false
}

extension View {
    /// Native iOS confirm alert. Use for non-destructive or destructive prompts
    /// that do not require type-to-confirm input.
    func confirmDialog(
        isPresented: Binding<Bool>,
        config: ConfirmDialogConfig,
        onConfirm: @escaping () -> Void,
        onCancel: (() -> Void)? = nil
    ) -> some View {
        self.alert(config.title, isPresented: isPresented) {
            Button(config.cancelLabel, role: .cancel) {
                onCancel?()
            }
            Button(
                config.confirmLabel,
                role: config.isDanger ? .destructive : nil
            ) {
                onConfirm()
            }
        } message: {
            Text(config.message)
        }
    }
}

// MARK: - Type-to-confirm dialog (custom)

struct TypeToConfirmDialog: View {
    let title: String
    let bodyText: String
    /// The exact string the user must type to enable confirm.
    let requiresText: String
    var confirmLabel: String = "Hapus"
    var cancelLabel: String = "Batal"
    var isDanger: Bool = true
    let onConfirm: () -> Void
    let onCancel: () -> Void

    @State private var typed: String = ""
    @FocusState private var focused: Bool

    private var matched: Bool {
        typed.trimmingCharacters(in: .whitespaces) == requiresText
    }

    var body: some View {
        ZStack {
            Color.black.opacity(0.42)
                .ignoresSafeArea()
                .onTapGesture { onCancel() }

            VStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 12) {
                    Text(title)
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                        .foregroundColor(.sgTextPrimary)

                    Text(bodyText)
                        .font(.custom("PlusJakartaSans-Medium", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .lineSpacing(3)
                        .fixedSize(horizontal: false, vertical: true)

                    VStack(alignment: .leading, spacing: 6) {
                        Text("Ketik \"\(requiresText)\" untuk konfirmasi")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                            .foregroundColor(.sgTextMuted)

                        TextField(requiresText, text: $typed)
                            .font(.custom("PlusJakartaSans-Regular", size: 15))
                            .foregroundColor(.sgTextPrimary)
                            .autocorrectionDisabled(true)
                            .textInputAutocapitalization(.never)
                            .focused($focused)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color.sgSurfaceAlt)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .strokeBorder(
                                        focused ? Color.sgPrimary : Color.sgBorder,
                                        lineWidth: 1.5
                                    )
                            )
                    }
                    .padding(.top, 4)
                }
                .padding(20)

                Divider().background(Color.sgBorder)

                HStack(spacing: 0) {
                    Button(action: onCancel) {
                        Text(cancelLabel)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                            .foregroundColor(.sgTextPrimary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                    }
                    .buttonStyle(.plain)

                    Rectangle()
                        .fill(Color.sgBorder)
                        .frame(width: 1, height: 52)

                    Button {
                        if matched { onConfirm() }
                    } label: {
                        Text(confirmLabel)
                            .font(.custom("PlusJakartaSans-Bold", size: 15))
                            .foregroundColor(matched ? (isDanger ? .sgDanger : .sgPrimary) : .sgTextSubtle)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                    }
                    .buttonStyle(.plain)
                    .disabled(!matched)
                }
            }
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
            .padding(.horizontal, 32)
            .shadow(color: Color.black.opacity(0.18), radius: 24, x: 0, y: 8)
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                focused = true
            }
        }
    }
}

extension View {
    /// Presents a type-to-confirm modal overlay. Caller controls `isPresented`.
    func typeToConfirmDialog(
        isPresented: Binding<Bool>,
        title: String,
        body: String,
        requiresText: String,
        confirmLabel: String = "Hapus",
        cancelLabel: String = "Batal",
        isDanger: Bool = true,
        onConfirm: @escaping () -> Void,
        onCancel: (() -> Void)? = nil
    ) -> some View {
        ZStack {
            self
            if isPresented.wrappedValue {
                TypeToConfirmDialog(
                    title: title,
                    bodyText: body,
                    requiresText: requiresText,
                    confirmLabel: confirmLabel,
                    cancelLabel: cancelLabel,
                    isDanger: isDanger,
                    onConfirm: {
                        isPresented.wrappedValue = false
                        onConfirm()
                    },
                    onCancel: {
                        isPresented.wrappedValue = false
                        onCancel?()
                    }
                )
                .transition(.opacity)
            }
        }
        .animation(.easeInOut(duration: 0.18), value: isPresented.wrappedValue)
    }
}

#Preview {
    StatefulPreviewWrapper(false) { shown in
        VStack(spacing: 16) {
            Button("Standard confirm") { shown.wrappedValue = true }
                .confirmDialog(
                    isPresented: shown,
                    config: .init(
                        title: "Hapus servis ini?",
                        message: "Riwayat ini akan dihapus permanen.",
                        confirmLabel: "Hapus",
                        isDanger: true
                    ),
                    onConfirm: {}
                )
        }
        .padding()
        .background(Color.sgBgWarm)
    }
}

// Tiny helper for previews
private struct StatefulPreviewWrapper<Value, Content: View>: View {
    @State private var value: Value
    let content: (Binding<Value>) -> Content
    init(_ initial: Value, @ViewBuilder content: @escaping (Binding<Value>) -> Content) {
        _value = State(initialValue: initial)
        self.content = content
    }
    var body: some View { content($value) }
}
