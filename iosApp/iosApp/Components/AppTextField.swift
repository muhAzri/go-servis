import SwiftUI
struct AppTextField: View {
    let label: String
    let placeholder: String
    @Binding var text: String
    var keyboardType: UIKeyboardType = .default

    @FocusState private var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                .foregroundColor(.sgTextMuted)

            ZStack(alignment: .leading) {
                if text.isEmpty {
                    Text(placeholder)
                        .font(.custom("PlusJakartaSans-Regular", size: 15))
                        .foregroundColor(.sgTextSubtle)
                        .padding(.horizontal, 16)
                }
                TextField("", text: $text)
                    .font(.custom("PlusJakartaSans-Regular", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .keyboardType(keyboardType)
                    .focused($isFocused)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(
                        isFocused ? Color.sgPrimary : Color.black.opacity(0.08),
                        lineWidth: 1.5
                    )
            )
        }
    }
}
