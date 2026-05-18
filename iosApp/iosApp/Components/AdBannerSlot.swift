import SwiftUI

struct AdBannerSlot: View {
    var maxHeight: CGFloat = 100

    var body: some View {
        AdsBannerView(maxHeight: maxHeight)
            .padding(.horizontal, 16)
    }
}

#Preview {
    AdBannerSlot().padding(.vertical)
}
