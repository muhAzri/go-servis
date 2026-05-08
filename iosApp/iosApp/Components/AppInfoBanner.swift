import SwiftUI
struct AppInfoBanner: View {
    let text: String
    var icon: String = "\u{f05a}"

    var body: some View {
        HStack(spacing: 10) {
            Text(icon)
                .font(.custom("FontAwesome6Free-Solid", size: 15))
                .foregroundColor(.sgTextMuted)
            Text(text)
                .font(.custom("PlusJakartaSans-Regular", size: 12))
                .foregroundColor(.sgTextMuted)
                .lineSpacing(2)
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
