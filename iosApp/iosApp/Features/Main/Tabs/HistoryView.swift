import SwiftUI

struct HistoryView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                TabHeader(subtitle: "0 servis tercatat", title: "Riwayat Servis")
            }
        }
    }
}

#Preview {
    HistoryView()
        .background(Color.sgBgWarm)
}
