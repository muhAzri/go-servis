import SwiftUI

struct GarasiView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                TabHeader(subtitle: "Halo, Budi 👋", title: "Garasi Saya")
            }
        }
    }
}

#Preview {
    GarasiView()
        .background(Color.sgBgWarm)
}
