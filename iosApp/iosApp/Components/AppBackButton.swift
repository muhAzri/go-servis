import SwiftUI

struct AppBackButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("\u{f053}")
                .font(.custom("FontAwesome6Free-Solid", size: 18))
                .foregroundColor(.sgTextPrimary)
                .frame(width: 40, height: 40)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: 12))
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .strokeBorder(Color.black.opacity(0.08), lineWidth: 1)
                )
        }
        .buttonStyle(.plain)
    }
}
