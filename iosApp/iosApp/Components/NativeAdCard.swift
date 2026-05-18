import SwiftUI

struct NativeAdCard: View {
    var body: some View {
        AdsNativeCardView()
            .frame(maxWidth: .infinity, minHeight: 320)
            .padding(.horizontal, 16)
    }
}

#Preview {
    NativeAdCard().padding(.vertical)
}
