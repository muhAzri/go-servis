import SwiftUI

enum BottomTab: Hashable, CaseIterable {
    case beranda, pengingat, add, riwayat, saya

    var icon: String {
        switch self {
        case .beranda:   return "\u{f015}"
        case .pengingat: return "\u{f0f3}"
        case .add:       return "\u{f067}"
        case .riwayat:   return "\u{f1da}"
        case .saya:      return "\u{f013}"
        }
    }

    var label: String {
        switch self {
        case .beranda:   return "Beranda"
        case .pengingat: return "Pengingat"
        case .add:       return ""
        case .riwayat:   return "Riwayat"
        case .saya:      return "Saya"
        }
    }
}

struct BottomNavBar: View {
    @Binding var selected: BottomTab
    var onSelect: (BottomTab) -> Void = { _ in }

    var body: some View {
        HStack(spacing: 0) {
            ForEach(BottomTab.allCases, id: \.self) { tab in
                if tab == .add {
                    AddTabItem(onTap: { onSelect(tab) })
                } else {
                    TabItem(
                        tab: tab,
                        active: tab == selected,
                        onTap: {
                            selected = tab
                            onSelect(tab)
                        }
                    )
                    .frame(maxWidth: .infinity)
                }
            }
        }
        .padding(EdgeInsets(top: 6, leading: 6, bottom: 18, trailing: 6))
        .frame(height: 80)
        .background(Color.sgSurface)
        .overlay(alignment: .top) {
            Rectangle()
                .fill(Color.sgBorder)
                .frame(height: 1)
        }
    }
}

private struct TabItem: View {
    let tab: BottomTab
    let active: Bool
    let onTap: () -> Void

    private var color: Color {
        active ? .sgPrimary : .sgTextSubtle
    }

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 2) {
                Text(tab.icon)
                    .font(.custom("FontAwesome6Free-Solid", size: 22))
                    .foregroundColor(color)
                Text(tab.label)
                    .font(.custom(
                        active ? "PlusJakartaSans-Bold" : "PlusJakartaSans-Medium",
                        size: 10
                    ))
                    .foregroundColor(color)
            }
            .padding(.vertical, 8)
            .frame(maxWidth: .infinity)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct AddTabItem: View {
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            ZStack {
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.sgPrimary)
                Text("\u{f067}")
                    .font(.custom("FontAwesome6Free-Solid", size: 26))
                    .foregroundColor(.white)
            }
            .frame(width: 52, height: 52)
            .shadow(color: Color.sgPrimary.opacity(0.33), radius: 14, x: 0, y: 6)
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    StatefulPreviewWrapper(BottomTab.beranda) { binding in
        VStack {
            Spacer()
            BottomNavBar(selected: binding)
        }
        .background(Color.sgBgWarm)
    }
}

private struct StatefulPreviewWrapper<Value, Content: View>: View {
    @State private var value: Value
    let content: (Binding<Value>) -> Content

    init(_ initial: Value, @ViewBuilder content: @escaping (Binding<Value>) -> Content) {
        self._value = State(initialValue: initial)
        self.content = content
    }

    var body: some View { content($value) }
}
