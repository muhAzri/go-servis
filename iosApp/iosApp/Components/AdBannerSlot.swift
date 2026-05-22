import SwiftUI

struct AdBannerSlot: View {
    var maxHeight: CGFloat = 100

    @State private var loadState: AdLoadState = .loading

    private var isLoaded: Bool { loadState == .loaded }

    var body: some View {
        AdsBannerView(maxHeight: maxHeight, state: $loadState)
            .padding(.horizontal, 16)
            .frame(height: isLoaded ? nil : 0)
            .clipped()
            .opacity(isLoaded ? 1 : 0)
            .animation(.easeOut(duration: 0.22), value: loadState)
    }
}

#Preview {
    AdBannerSlot().padding(.vertical)
}
