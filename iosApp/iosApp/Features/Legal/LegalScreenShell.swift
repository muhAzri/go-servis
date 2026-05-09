import SwiftUI

struct LegalScreenShell<Content: View>: View {
    let title: String
    let onBack: () -> Void
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 12) {
                AppBackButton(action: onBack)
                Text(title)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 17))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
            }
            .padding(EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16))

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    content()
                }
                .padding(EdgeInsets(top: 8, leading: 24, bottom: 28, trailing: 24))
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(Color.sgBgWarm)
        .navigationBarBackButtonHidden(true)
    }
}

struct LegalSectionTitle: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
            .foregroundColor(.sgTextPrimary)
            .padding(.top, 12)
            .padding(.bottom, 8)
    }
}

struct LegalParagraph: View {
    let heading: String
    let text: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(heading)
                .font(.custom("PlusJakartaSans-Bold", size: 14))
                .foregroundColor(.sgTextPrimary)
            Text(text)
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
                .lineSpacing(4)
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding(.bottom, 14)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
