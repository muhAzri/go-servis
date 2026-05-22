import SwiftUI

/// Search field inside a rounded card. Magnifying-glass icon + clear button + optional trailing slot.
struct StickySearchHeader<Trailing: View>: View {
    let placeholder: String
    @Binding var text: String
    @ViewBuilder var trailing: () -> Trailing

    @FocusState private var focused: Bool

    var body: some View {
        HStack(spacing: 10) {
            HStack(spacing: 8) {
                Text("\u{f002}")
                    .font(.custom("FontAwesome6Free-Solid", size: 13))
                    .foregroundColor(.sgTextSubtle)

                ZStack(alignment: .leading) {
                    if text.isEmpty {
                        Text(placeholder)
                            .font(.custom("PlusJakartaSans-Medium", size: 14))
                            .foregroundColor(.sgTextSubtle)
                    }
                    TextField("", text: $text)
                        .font(.custom("PlusJakartaSans-Medium", size: 14))
                        .foregroundColor(.sgTextPrimary)
                        .autocorrectionDisabled(true)
                        .focused($focused)
                }

                if !text.isEmpty {
                    Button { text = "" } label: {
                        Text("\u{f00d}")
                            .font(.custom("FontAwesome6Free-Solid", size: 12))
                            .foregroundColor(.sgTextSubtle)
                            .frame(width: 22, height: 22)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(focused ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
            )
            .frame(maxWidth: .infinity)

            trailing()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .background(Color.sgBgWarm)
    }
}

extension StickySearchHeader where Trailing == EmptyView {
    init(placeholder: String, text: Binding<String>) {
        self.placeholder = placeholder
        self._text = text
        self.trailing = { EmptyView() }
    }
}

#Preview {
    StatefulSearch()
}

private struct StatefulSearch: View {
    @State var q = ""
    var body: some View {
        VStack(spacing: 0) {
            StickySearchHeader(placeholder: "Cari servis...", text: $q) {
                SortChip(label: "Terbaru", onTap: {})
            }
            Spacer()
        }
        .background(Color.sgBgWarm)
    }
}
