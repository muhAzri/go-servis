import SwiftUI

struct IconBadge: View {
    let iconUnicode: String
    let foreground: Color
    let background: Color
    var size: CGFloat = 44
    var iconSize: CGFloat = 22
    var corner: CGFloat = 12

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: corner)
                .fill(background)
                .frame(width: size, height: size)
            Text(iconUnicode)
                .font(.custom("FontAwesome6Free-Solid", size: iconSize))
                .foregroundColor(foreground)
        }
    }
}

#Preview {
    HStack(spacing: 12) {
        IconBadge(iconUnicode: "\u{f613}", foreground: .sgWarning, background: .sgWarningSoft)
        IconBadge(iconUnicode: "\u{f21c}", foreground: .sgPrimary, background: .sgPrimarySoft)
        IconBadge(iconUnicode: "\u{f5df}", foreground: .sgDanger, background: .sgDangerSoft)
    }
    .padding()
}
