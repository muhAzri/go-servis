import SwiftUI

struct NativeAdCard: View {
    @State private var loadState: AdLoadState = .loading

    private var isLoaded: Bool { loadState == .loaded }

    var body: some View {
        AdsNativeCardView(state: $loadState)
            .padding(.horizontal, 16)
            .frame(maxWidth: .infinity)
            .frame(height: isLoaded ? nil : 0)
            .clipped()
            .opacity(isLoaded ? 1 : 0)
            .animation(.easeOut(duration: 0.22), value: loadState)
    }
}

#Preview {
    NativeAdCard().padding(.vertical)
}
