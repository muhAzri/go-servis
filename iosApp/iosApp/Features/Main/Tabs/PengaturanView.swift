import SwiftUI

struct PengaturanView: View {
    var onOpenTestScreen: () -> Void = {}

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                TabHeader(subtitle: nil, title: "Pengaturan")

                Button(action: onOpenTestScreen) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Test Screen")
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Text("Buka layar debug & uji iklan")
                            .font(.custom("PlusJakartaSans-Medium", size: 12))
                            .foregroundColor(.sgTextMuted)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16))
                    .background(
                        RoundedRectangle(cornerRadius: 18, style: .continuous)
                            .fill(Color.sgSurface)
                    )
                }
                .buttonStyle(.plain)
                .padding(.horizontal, 16)
            }
        }
    }
}

#Preview {
    PengaturanView()
        .background(Color.sgBgWarm)
}
