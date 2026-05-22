import SwiftUI

// MARK: - PHLabel

struct PHLabel: View {
    let text: String
    var hint: String? = nil

    var body: some View {
        HStack(spacing: 8) {
            Text(text.uppercased())
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(0.6)
                .foregroundColor(.sgTextMuted)
            if let hint, !hint.isEmpty {
                Text(hint)
                    .font(.custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextSubtle)
            }
            Spacer(minLength: 0)
        }
    }
}

// MARK: - PHInput

struct PHInput: View {
    let placeholder: String
    @Binding var text: String
    var iconUnicode: String? = nil
    var suffix: String? = nil
    var keyboardType: UIKeyboardType = .default
    var isSecure: Bool = false
    var disabled: Bool = false

    @FocusState private var focused: Bool

    var body: some View {
        HStack(spacing: 10) {
            if let icon = iconUnicode {
                Text(icon)
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .frame(width: 18)
            }

            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .font(.custom("PlusJakartaSans-Regular", size: 14))
                        .foregroundColor(.sgTextSubtle)
                }
                Group {
                    if isSecure {
                        SecureField("", text: $text)
                    } else {
                        TextField("", text: $text)
                    }
                }
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextPrimary)
                .keyboardType(keyboardType)
                .focused($focused)
                .disabled(disabled)
            }

            if let suffix, !suffix.isEmpty {
                Text(suffix)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextMuted)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(disabled ? Color.sgSurfaceAlt : Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .strokeBorder(
                    focused ? Color.sgPrimary : Color.sgBorder,
                    lineWidth: 1.5
                )
        )
    }
}

// MARK: - PHPickerRow (read-only tappable row with chevron)

struct PHPickerRow: View {
    let value: String?
    let placeholder: String
    var iconUnicode: String? = nil
    var trailing: String? = nil
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 10) {
                if let icon = iconUnicode {
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 18)
                }

                Text((value?.isEmpty ?? true) ? placeholder : value!)
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor((value?.isEmpty ?? true) ? .sgTextSubtle : .sgTextPrimary)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)

                if let trailing, !trailing.isEmpty {
                    Text(trailing)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                        .foregroundColor(.sgTextMuted)
                }

                Text("\u{f078}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(Color.sgBorder, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }
}

// MARK: - PHSegmented (2-3 options)

struct PHSegmentedOption: Identifiable, Equatable {
    let id: String
    let label: String
    var iconUnicode: String? = nil
}

struct PHSegmented: View {
    let options: [PHSegmentedOption]
    @Binding var selectedId: String

    var body: some View {
        HStack(spacing: 6) {
            ForEach(options) { option in
                let active = option.id == selectedId
                Button {
                    selectedId = option.id
                } label: {
                    HStack(spacing: 6) {
                        if let icon = option.iconUnicode {
                            Text(icon)
                                .font(.custom("FontAwesome6Free-Solid", size: 12))
                        }
                        Text(option.label)
                            .font(.custom("PlusJakartaSans-Bold", size: 13))
                    }
                    .foregroundColor(active ? .white : .sgTextPrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 38)
                    .background(active ? Color.sgPrimary : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .buttonStyle(.plain)
            }
        }
        .padding(4)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

#Preview {
    StatefulPreviewWrapper2("km") { sel in
        StatefulPreviewWrapper2("") { text in
            VStack(alignment: .leading, spacing: 12) {
                PHLabel(text: "Odometer", hint: "wajib")
                PHInput(placeholder: "12.345", text: text, iconUnicode: "\u{f624}", suffix: "km", keyboardType: .numberPad)
                PHLabel(text: "Tanggal")
                PHPickerRow(value: nil, placeholder: "Pilih tanggal", iconUnicode: "\u{f783}", onTap: {})
                PHLabel(text: "Trigger")
                PHSegmented(
                    options: [
                        .init(id: "km", label: "KM", iconUnicode: "\u{f624}"),
                        .init(id: "date", label: "Tanggal", iconUnicode: "\u{f783}"),
                        .init(id: "both", label: "Keduanya"),
                    ],
                    selectedId: sel
                )
            }
            .padding()
            .background(Color.sgBgWarm)
        }
    }
}

private struct StatefulPreviewWrapper2<Value, Content: View>: View {
    @State private var value: Value
    let content: (Binding<Value>) -> Content
    init(_ initial: Value, @ViewBuilder content: @escaping (Binding<Value>) -> Content) {
        _value = State(initialValue: initial)
        self.content = content
    }
    var body: some View { content($value) }
}
