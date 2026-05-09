import SwiftUI

private let faq: [(String, String)] = [
    ("Bagaimana cara menambah kendaraan?", "Tap tombol + di tab tengah, lalu pilih jenis (motor/mobil), isi merk, model, plat, dan KM saat ini. Selesai."),
    ("Apakah saya perlu update KM tiap hari?", "Tidak. Update saat kamu ingat saja — minimal sebulan sekali agar pengingat akurat."),
    ("Data saya hilang setelah uninstall?", "Iya, karena data disimpan lokal di perangkat dan kami tidak menyimpan ke server. Pastikan ekspor CSV sebelum uninstall jika ingin menyimpan riwayat."),
    ("Notifikasi tidak muncul?", "Pastikan izin notifikasi aktif di Pengaturan HP > ServisGo > Notifikasi."),
    ("Apakah ServisGo gratis?", "Ya, gratis dengan iklan. Kami menjaga privasimu — tidak ada login, tidak ada tracking lintas aplikasi dari kami."),
]

struct HelpView: View {
    let onBack: () -> Void

    var body: some View {
        LegalScreenShell(title: "Bantuan & FAQ", onBack: onBack) {
            VStack(spacing: 0) {
                ForEach(Array(faq.enumerated()), id: \.offset) { index, item in
                    faqRow(question: item.0, answer: item.1)
                    if index < faq.count - 1 {
                        Rectangle().fill(Color.sgBorder).frame(height: 1)
                    }
                }
            }
            .padding(.horizontal, 16)
            .background(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.sgSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )

            VStack(alignment: .leading, spacing: 4) {
                Text("Masih butuh bantuan?")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text("Email kami di muhammad.azri.f.s@gmail.com — biasanya dijawab dalam 1×24 jam.")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(4)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(14)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(Color.sgPrimarySofter)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .strokeBorder(Color.sgPrimary.opacity(0.18), lineWidth: 1)
            )
            .padding(.top, 16)
        }
    }

    private func faqRow(question: String, answer: String) -> some View {
        HStack(alignment: .top, spacing: 10) {
            Text("\u{f05a}")
                .font(.custom("FontAwesome6Free-Solid", size: 16))
                .foregroundColor(.sgPrimary)
                .padding(.top, 2)
            VStack(alignment: .leading, spacing: 4) {
                Text(question)
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgTextPrimary)
                Text(answer)
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(4)
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
        .padding(.vertical, 14)
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

#Preview {
    HelpView(onBack: {})
}
