import SwiftUI

struct TagBadge: View {
    let tag: ComponentTag

    private var colors: (fg: Color, bg: Color) {
        switch tag {
        case .core: return (.sgPrimary, .sgPrimarySoft)
        case .plus: return (.sgWarning, .sgWarningSoft)
        case .pro:  return (.sgTextMuted, .sgSurfaceAlt)
        }
    }

    var body: some View {
        Text(tag.label.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 9))
            .kerning(0.6)
            .foregroundColor(colors.fg)
            .padding(.horizontal, 7)
            .padding(.vertical, 3)
            .background(colors.bg)
            .clipShape(Capsule())
    }
}

struct SubtypeBadge: View {
    let label: String

    var body: some View {
        Text(label.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 9))
            .kerning(0.6)
            .foregroundColor(.sgPrimary)
            .padding(.horizontal, 7)
            .padding(.vertical, 3)
            .background(Color.sgPrimarySoft)
            .clipShape(Capsule())
    }
}

struct ToggleSwitch: View {
    let on: Bool

    var body: some View {
        ZStack(alignment: on ? .trailing : .leading) {
            RoundedRectangle(cornerRadius: 13)
                .fill(on ? Color.sgPrimary : Color.sgBorder.opacity(2))
                .frame(width: 44, height: 26)
            Circle()
                .fill(Color.white)
                .frame(width: 20, height: 20)
                .shadow(color: .black.opacity(0.18), radius: 2, x: 0, y: 1)
                .padding(.horizontal, 3)
        }
        .animation(.easeInOut(duration: 0.15), value: on)
    }
}

struct DashedBorder: ViewModifier {
    let cornerRadius: CGFloat
    let lineWidth: CGFloat
    let color: Color

    func body(content: Content) -> some View {
        content.overlay(
            RoundedRectangle(cornerRadius: cornerRadius)
                .strokeBorder(style: StrokeStyle(lineWidth: lineWidth, dash: [6, 4]))
                .foregroundColor(color)
        )
    }
}

extension View {
    func dashedBorder(cornerRadius: CGFloat, lineWidth: CGFloat = 1.5, color: Color = .sgBorder) -> some View {
        self.modifier(DashedBorder(cornerRadius: cornerRadius, lineWidth: lineWidth, color: color))
    }
}
