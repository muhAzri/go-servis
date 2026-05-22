import SwiftUI

/// Full-screen form wrapper:
/// - Top bar: leading dismiss (✕), centered-left title, trailing "Simpan".
/// - Scrollable body slot.
/// - Optional sticky footer slot.
struct FormShell<Body: View, Footer: View>: View {
    let title: String
    var saveLabel: String = "Simpan"
    var canSave: Bool = true
    var isSaving: Bool = false
    let onDismiss: () -> Void
    let onSave: () -> Void
    @ViewBuilder var bodyContent: () -> Body
    @ViewBuilder var footer: () -> Footer

    var body: some View {
        VStack(spacing: 0) {
            topBar
            ScrollView {
                VStack(alignment: .leading, spacing: 14) {
                    bodyContent()
                }
                .padding(.horizontal, 16)
                .padding(.top, 16)
                .padding(.bottom, 32)
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .background(Color.sgBgWarm)

            let footerView = footer()
            if !(footerView is EmptyView) {
                VStack(spacing: 0) {
                    Divider().background(Color.sgBorder)
                    footerView
                        .padding(.horizontal, 16)
                        .padding(.top, 12)
                        .padding(.bottom, 20)
                        .background(Color.sgSurface)
                }
            }
        }
        .background(Color.sgBgWarm.ignoresSafeArea())
    }

    private var topBar: some View {
        HStack(spacing: 12) {
            Button(action: onDismiss) {
                Text("\u{f00d}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextPrimary)
                    .frame(width: 40, height: 40)
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .strokeBorder(Color.sgBorder, lineWidth: 1)
                    )
            }
            .buttonStyle(.plain)

            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.2)
                .frame(maxWidth: .infinity, alignment: .leading)

            Button(action: {
                guard canSave, !isSaving else { return }
                onSave()
            }) {
                HStack(spacing: 6) {
                    if isSaving {
                        ProgressView()
                            .progressViewStyle(.circular)
                            .tint(.white)
                            .scaleEffect(0.8)
                    }
                    Text(saveLabel)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 16)
                .frame(height: 40)
                .background(Color.sgPrimary.opacity(canSave && !isSaving ? 1.0 : 0.45))
                .clipShape(RoundedRectangle(cornerRadius: 12))
            }
            .buttonStyle(.plain)
            .disabled(!canSave || isSaving)
        }
        .padding(.horizontal, 16)
        .padding(.top, 12)
        .padding(.bottom, 12)
        .background(Color.sgSurface)
        .overlay(
            Rectangle()
                .fill(Color.sgBorder)
                .frame(height: 1),
            alignment: .bottom
        )
    }
}

extension FormShell where Footer == EmptyView {
    init(
        title: String,
        saveLabel: String = "Simpan",
        canSave: Bool = true,
        isSaving: Bool = false,
        onDismiss: @escaping () -> Void,
        onSave: @escaping () -> Void,
        @ViewBuilder bodyContent: @escaping () -> Body
    ) {
        self.title = title
        self.saveLabel = saveLabel
        self.canSave = canSave
        self.isSaving = isSaving
        self.onDismiss = onDismiss
        self.onSave = onSave
        self.bodyContent = bodyContent
        self.footer = { EmptyView() }
    }
}

#Preview {
    FormShell(
        title: "Tambah Servis",
        canSave: true,
        onDismiss: {},
        onSave: {}
    ) {
        ForEach(0..<6, id: \.self) { _ in
            RoundedRectangle(cornerRadius: 14)
                .fill(Color.sgSurface)
                .frame(height: 64)
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgBorder, lineWidth: 1)
                )
        }
    }
}
