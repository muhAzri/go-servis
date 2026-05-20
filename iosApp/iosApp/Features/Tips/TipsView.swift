import SwiftUI

struct TipsView: View {
    var onOpenTipDetail: () -> Void = {}

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                Button(action: onOpenTipDetail) {
                    HeroTip()
                }
                .buttonStyle(.plain)
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 14)

                AdBannerSlot()
                    .padding(.bottom, 14)

                VStack(spacing: 8) {
                    TipRow(tag: "Oli",     title: "Kapan harus ganti oli motor matic?",   readTime: "2 menit", onTap: onOpenTipDetail)
                    TipRow(tag: "Ban",     title: "Cara cek tekanan ban yang benar",       readTime: "3 menit", onTap: onOpenTipDetail)
                    TipRow(tag: "Aki",     title: "Tanda-tanda aki mobil mau soak",        readTime: "4 menit", onTap: onOpenTipDetail)
                    TipRow(tag: "Tips",    title: "5 hal sebelum mudik dengan motor",      readTime: "5 menit", onTap: onOpenTipDetail)
                    TipRow(tag: "Servis",  title: "Beda servis berkala 1.000 vs 5.000 km", readTime: "3 menit", onTap: onOpenTipDetail)
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Tips Perawatan")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct HeroTip: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("TIP HARI INI")
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(0.5)
                .foregroundColor(.white.opacity(0.85))
            Text("Cek tekanan ban tiap 2 minggu — irit BBM hingga 7%")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                .foregroundColor(.white)
                .kerning(-0.4)
                .lineSpacing(2)
            Text("3 menit baca · oleh Tim ServisGo")
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.white.opacity(0.85))
                .padding(.top, 2)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(20)
        .background(
            LinearGradient(
                colors: [.sgPrimary, .sgPrimaryDark],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 22))
    }
}

private struct TipRow: View {
    let tag: String
    let title: String
    let readTime: String
    var onTap: () -> Void = {}

    var body: some View {
        Button(action: onTap) {
            rowBody
        }
        .buttonStyle(.plain)
    }

    private var rowBody: some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.sgSurfaceAlt)
                    .frame(width: 56, height: 56)
                Text("\u{f0eb}")
                    .font(.custom("FontAwesome6Free-Solid", size: 26))
                    .foregroundColor(.sgTextMuted)
            }
            VStack(alignment: .leading, spacing: 6) {
                Text(tag.uppercased())
                    .font(.custom("PlusJakartaSans-Bold", size: 10))
                    .kerning(0.5)
                    .foregroundColor(.sgPrimary)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(Color.sgPrimarySoft)
                    .clipShape(RoundedRectangle(cornerRadius: 4))
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                    .lineLimit(2)
                Text(readTime)
                    .font(.custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextSubtle)
            }
            Spacer()
        }
        .padding(14)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

#Preview {
    NavigationStack { TipsView() }
}
