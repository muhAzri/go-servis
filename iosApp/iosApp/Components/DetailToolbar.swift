import SwiftUI

enum DetailToolbarTone {
    case neutral, primary, danger

    var fg: Color {
        switch self {
        case .neutral: return .sgTextPrimary
        case .primary: return .sgPrimary
        case .danger:  return .sgDanger
        }
    }
}

struct DetailToolbarAction: Identifiable {
    let id: String
    let iconUnicode: String
    var tone: DetailToolbarTone = .neutral
    let onTap: () -> Void

    init(
        id: String = UUID().uuidString,
        iconUnicode: String,
        tone: DetailToolbarTone = .neutral,
        onTap: @escaping () -> Void
    ) {
        self.id = id
        self.iconUnicode = iconUnicode
        self.tone = tone
        self.onTap = onTap
    }
}

/// Toolbar pattern for detail views: rounded back button (40×40), optional centered-left title,
/// and a row of icon action buttons.
struct DetailToolbar: View {
    let onBack: () -> Void
    var title: String? = nil
    var actions: [DetailToolbarAction] = []

    var body: some View {
        HStack(spacing: 10) {
            Button(action: onBack) {
                Text("\u{f053}")
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

            if let title, !title.isEmpty {
                Text(title)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 17))
                    .foregroundColor(.sgTextPrimary)
                    .lineLimit(1)
                    .frame(maxWidth: .infinity, alignment: .leading)
            } else {
                Spacer(minLength: 0)
            }

            HStack(spacing: 8) {
                ForEach(actions) { action in
                    Button(action: action.onTap) {
                        Text(action.iconUnicode)
                            .font(.custom("FontAwesome6Free-Solid", size: 15))
                            .foregroundColor(action.tone.fg)
                            .frame(width: 40, height: 40)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                    }
                    .buttonStyle(.plain)
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
    }
}

#Preview {
    VStack {
        DetailToolbar(
            onBack: {},
            title: "Detail Servis",
            actions: [
                .init(iconUnicode: "\u{f1e0}", tone: .neutral, onTap: {}),
                .init(iconUnicode: "\u{f044}", tone: .primary, onTap: {}),
                .init(iconUnicode: "\u{f1f8}", tone: .danger, onTap: {}),
            ]
        )
        Spacer()
    }
    .background(Color.sgBgWarm)
}
