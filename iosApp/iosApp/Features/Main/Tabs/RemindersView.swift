import SwiftUI

struct RemindersView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                TabHeader(subtitle: "0 pengingat aktif", title: "Pengingat Servis")
            }
        }
    }
}

#Preview {
    RemindersView()
        .background(Color.sgBgWarm)
}
