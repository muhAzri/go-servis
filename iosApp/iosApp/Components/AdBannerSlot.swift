import SwiftUI

struct AdBannerSlot: View {
    var maxHeight: CGFloat = 100

    @State private var loadState: AdLoadState = .loading

    var body: some View {
        AdsBannerView(maxHeight: maxHeight, state: $loadState)
            .padding(.horizontal, loadState == .loaded ? 16 : 0)
            .opacity(loadState == .loaded ? 1 : 0)
            .animation(.easeOut(duration: 0.22), value: loadState)
    }
}

#Preview {
    AdBannerSlot().padding(.vertical)
}
