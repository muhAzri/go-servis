import SwiftUI

struct NativeAdCard: View {
    @State private var loadState: AdLoadState = .loading

    private var isLoaded: Bool { loadState == .loaded }

    var body: some View {
        AdsNativeCardView(state: $loadState)
            .frame(maxWidth: .infinity)
            .frame(height: isLoaded ? nil : 0)
            .clipped()
            .padding(.horizontal, isLoaded ? 16 : 0)
            .opacity(isLoaded ? 1 : 0)
            .animation(.easeOut(duration: 0.22), value: loadState)
    }
}

#Preview {
    NativeAdCard().padding(.vertical)
}
