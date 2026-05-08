import SwiftUI
struct AppButton: View {
    let title: String
    let action: () -> Void
    var isEnabled: Bool = true
    var trailingIcon: String? = nil

    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 16))
                    .foregroundColor(.white)
                if let icon = trailingIcon {
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(Color.sgPrimary.opacity(isEnabled ? 1.0 : 0.5))
            .clipShape(RoundedRectangle(cornerRadius: 18))
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled)
    }
}
