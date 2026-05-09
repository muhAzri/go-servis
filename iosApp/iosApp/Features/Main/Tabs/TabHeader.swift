import SwiftUI

struct TabHeader: View {
    let subtitle: String?
    let title: String

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            if let subtitle {
                Text(subtitle)
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
            }
            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.4)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(EdgeInsets(top: 12, leading: 20, bottom: 16, trailing: 20))
    }
}
