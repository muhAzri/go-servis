import SwiftUI

private struct RelatedTip: Identifiable {
    let id = UUID()
    let title: String
    let readTime: String
}

private let relatedTips: [RelatedTip] = [
    .init(title: "Cara cek oli motor manual", readTime: "3 menit"),
    .init(title: "Beda oli mineral vs sintetik", readTime: "4 menit"),
]

struct TipsDetailView: View {
    var onOpenAddService: () -> Void = {}
    var onOpenRelated: (String) -> Void = { _ in }

    @State private var bookmarked: Bool = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HeroImage()
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 16)

                HStack(spacing: 8) {
                    CategoryChip(
                        label: "OLI",
                        iconUnicode: "\u{f613}",
                        color: Color(red: 0.91, green: 0.61, blue: 0.18)
                    )
                    Text("·").foregroundColor(.sgTextSubtle)
                    Text("2 menit baca")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                .padding(.horizontal, 20)

                Text("Kapan harus ganti oli motor matic?")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.5)
                    .lineSpacing(2)
                    .padding(.horizontal, 20)
                    .padding(.top, 12)
                    .frame(maxWidth: .infinity, alignment: .leading)

                Text("Dipublikasi 5 Mei 2026")
                    .font(.custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextSubtle)
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                    .frame(maxWidth: .infinity, alignment: .leading)

                Rectangle().fill(Color.sgBorder)
                    .frame(height: 1)
                    .padding(.horizontal, 20)
                    .padding(.top, 12)

                ArticleBody()
                    .padding(.horizontal, 20)
                    .padding(.top, 16)

                ContextBanner(
                    title: "Rekomendasi untuk Beat Hitam",
                    body: "Beat Hitam butuh ganti oli dalam 580 km. Catat sekarang supaya tepat waktu.",
                    iconUnicode: "\u{f0eb}",
                    tone: .info,
                    ctaLabel: "Catat servis sekarang →",
                    onCta: onOpenAddService
                )
                .padding(.horizontal, 16)
                .padding(.top, 20)

                Text("TIPS TERKAIT")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                    .kerning(1)
                    .foregroundColor(.sgTextMuted)
                    .padding(.horizontal, 20)
                    .padding(.top, 24)
                    .padding(.bottom, 8)
                    .frame(maxWidth: .infinity, alignment: .leading)

                VStack(spacing: 8) {
                    ForEach(relatedTips) { tip in
                        RelatedRow(tip: tip, onTap: { onOpenRelated(tip.title) })
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
        }
        .background(Color.sgBgWarm)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button(action: { bookmarked.toggle() }) {
                    Image(systemName: bookmarked ? "star.fill" : "star")
                        .foregroundColor(bookmarked ? .sgWarning : .sgTextPrimary)
                }
                Button(action: {}) {
                    Image(systemName: "square.and.arrow.up")
                }
            }
        }
    }
}

// MARK: - Subviews

private struct HeroImage: View {
    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.sgPrimarySoft, Color.sgWarningSoft],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            Text("[ tip cover · 16:9 ]")
                .font(.system(size: 11, weight: .medium, design: .monospaced))
                .foregroundColor(.sgTextSubtle)
        }
        .aspectRatio(16.0/9.0, contentMode: .fit)
        .clipShape(RoundedRectangle(cornerRadius: 18))
    }
}

private struct CategoryChip: View {
    let label: String
    let iconUnicode: String
    let color: Color

    var body: some View {
        HStack(spacing: 6) {
            Text(iconUnicode)
                .font(.custom("FontAwesome6Free-Solid", size: 11))
                .foregroundColor(color)
            Text(label)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(0.6)
                .foregroundColor(color)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 5)
        .background(color.opacity(0.14))
        .clipShape(Capsule())
    }
}

private struct ArticleBody: View {
    private let paragraphs = [
        "Oli matic punya peran ganda — melumasi mesin dan mendinginkan CVT. Karena dua tugas itu, intervalnya lebih pendek dibanding motor manual: 2.000 km atau 2 bulan, mana yang tercapai lebih dulu.",
        "Kalau motor sering kena macet ibu kota, hitung interval real-nya: 1 jam macet kira-kira setara 25 km perjalanan biasa. Beat yang sering nganterin anak sekolah pagi-pagi bisa butuh ganti oli sekitar 1.500 km — lebih awal dari rekomendasi pabrik.",
        "Cara cek sendiri: tarik dipstick (kalau matic Honda ada di sisi kanan mesin), lap dengan kain, lalu cocokin lagi. Oli yang masih bagus warnanya kuning kecokelatan dan tidak ada bau bensin. Kalau warnanya hitam pekat atau mengandung partikel berkilau, segera ganti.",
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            ForEach(paragraphs, id: \.self) { p in
                Text(p)
                    .font(.custom("PlusJakartaSans-Medium", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .lineSpacing(6)
            }
        }
    }
}

private struct RelatedRow: View {
    let tip: RelatedTip
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                Text("\u{f0eb}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgWarning)
                    .frame(width: 18)
                VStack(alignment: .leading, spacing: 2) {
                    Text(tip.title)
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                    Text(tip.readTime)
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    NavigationStack { TipsDetailView() }
}
