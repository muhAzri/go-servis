import SwiftUI

struct HomeView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                TabHeader(subtitle: "Halo, Budi 👋", title: "Garasi Saya")
            }
        }
    }
}

#Preview {
    HomeView()
        .background(Color.sgBgWarm)
}
