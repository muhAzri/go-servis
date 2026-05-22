import SwiftUI

enum ActionSheetTone {
    case neutral
    case primary
    case danger

    var foreground: Color {
        switch self {
        case .neutral: return .sgTextPrimary
        case .primary: return .sgPrimary
        case .danger:  return .sgDanger
        }
    }

    var iconBg: Color {
        switch self {
        case .neutral: return .sgSurfaceAlt
        case .primary: return .sgPrimarySoft
        case .danger:  return .sgDangerSoft
        }
    }

    var iconFg: Color {
        switch self {
        case .neutral: return .sgTextMuted
        case .primary: return .sgPrimary
        case .danger:  return .sgDanger
        }
    }
}

struct ActionSheetOption: Identifiable, Equatable {
    let id: String
    let iconUnicode: String?
    let label: String
    let subtitle: String?
    let value: String
    var tone: ActionSheetTone

    static func == (lhs: ActionSheetOption, rhs: ActionSheetOption) -> Bool {
        lhs.id == rhs.id
    }

    init(
        id: String,
        iconUnicode: String? = nil,
        label: String,
        subtitle: String? = nil,
        value: String,
        tone: ActionSheetTone = .neutral
    ) {
        self.id = id
        self.iconUnicode = iconUnicode
        self.label = label
        self.subtitle = subtitle
        self.value = value
        self.tone = tone
    }
}

enum ActionSheetSelectionMode {
    case tap   // tap-to-fire, no checkmark
    case radio // shows checkmark on the matching value
}

struct ActionSheetView: View {
    let title: String
    var subtitle: String? = nil
    let options: [ActionSheetOption]
    var selectionMode: ActionSheetSelectionMode = .tap
    var selectedValue: String? = nil
    var cancelLabel: String = "Batal"
    let onSelect: (String) -> Void
    var onCancel: (() -> Void)? = nil

    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 0) {
            // Drag indicator
            Capsule()
                .fill(Color.sgBorder)
                .frame(width: 40, height: 4)
                .padding(.top, 8)
                .padding(.bottom, 12)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                if let subtitle, !subtitle.isEmpty {
                    Text(subtitle)
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 20)
            .padding(.bottom, 12)

            ScrollView {
                VStack(spacing: 6) {
                    ForEach(options) { option in
                        Button {
                            onSelect(option.value)
                            dismiss()
                        } label: {
                            row(option)
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 8)
            }

            Button {
                onCancel?()
                dismiss()
            } label: {
                Text(cancelLabel)
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.sgSurfaceAlt)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 16)
            .padding(.top, 4)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
    }

    @ViewBuilder
    private func row(_ option: ActionSheetOption) -> some View {
        HStack(spacing: 12) {
            if let icon = option.iconUnicode {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(option.tone.iconBg)
                        .frame(width: 36, height: 36)
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(option.tone.iconFg)
                }
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(option.label)
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(option.tone.foreground)
                if let subtitle = option.subtitle {
                    Text(subtitle)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            if selectionMode == .radio, selectedValue == option.value {
                Text("\u{f00c}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgPrimary)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(Color.sgSurfaceAlt.opacity(0.5))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}

#Preview {
    Color.sgBgWarm.sheet(isPresented: .constant(true)) {
        ActionSheetView(
            title: "Aksi servis",
            subtitle: "Pilih salah satu",
            options: [
                .init(id: "edit", iconUnicode: "\u{f044}", label: "Edit", value: "edit", tone: .primary),
                .init(id: "share", iconUnicode: "\u{f1e0}", label: "Bagikan", value: "share"),
                .init(id: "delete", iconUnicode: "\u{f1f8}", label: "Hapus", subtitle: "Tidak bisa dikembalikan", value: "delete", tone: .danger),
            ],
            onSelect: { _ in }
        )
        .presentationDetents([.medium])
    }
}
