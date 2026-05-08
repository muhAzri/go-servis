import SwiftUI
struct OnboardingStepHeader: View {
    let stepLabel: String
    let title: String
    let subtitle: String
    let onBack: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            AppBackButton(action: onBack)
                .padding(.leading, 12)
                .padding(.top, 8)

            VStack(alignment: .leading, spacing: 0) {
                Text(stepLabel)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextMuted)
                Spacer().frame(height: 6)
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 26))
                    .foregroundColor(.sgTextPrimary)
                    .lineSpacing(2)
                    .fixedSize(horizontal: false, vertical: true)
                Spacer().frame(height: 6)
                Text(subtitle)
                    .font(.custom("PlusJakartaSans-Regular", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(3)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.horizontal, 24)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
