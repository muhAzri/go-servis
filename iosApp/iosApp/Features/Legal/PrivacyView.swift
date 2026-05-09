import SwiftUI

private let summaryBullets = [
    "Data kendaraan & servis disimpan lokal di HP kamu",
    "Tidak ada login, tidak ada akun",
    "Iklan ditampilkan via jaringan ad mediation pihak ketiga",
    "Tidak ada pelacakan lintas aplikasi dari kami",
]

private let sections: [(String, String)] = [
    ("1. Data yang kami kumpulkan", "Aplikasi ini bersifat lokal-first. Data kendaraan, KM, riwayat servis, dan pengingat disimpan di penyimpanan lokal perangkat. Kami tidak mengirim data tersebut ke server kami."),
    ("2. Iklan & Pihak Ketiga", "Kami menggunakan jaringan iklan dengan mediation untuk monetisasi (beberapa SDK pihak ketiga seperti AdMob, Yandex Ads, dan jaringan lain dapat ikut serta dalam mediation). Jaringan ini dapat mengakses identifier iklan perangkat (AAID/IDFA) sesuai kebijakan masing-masing. Kamu bisa mereset atau menonaktifkan personalisasi iklan via pengaturan sistem."),
    ("3. Notifikasi", "Notifikasi pengingat servis dijalankan secara lokal — tidak melalui server. Kamu bisa mengatur atau menonaktifkan kapan saja di Pengaturan > Notifikasi."),
    ("4. Anak di Bawah Umur", "Aplikasi ini tidak ditujukan untuk pengguna di bawah 13 tahun. Kami tidak sengaja mengumpulkan data dari anak-anak."),
    ("5. Perubahan Kebijakan", "Kami dapat memperbarui kebijakan ini sesekali. Kamu akan diberitahu melalui notifikasi in-app jika ada perubahan signifikan."),
    ("6. Kontak", "Pertanyaan privasi: muhammad.azri.f.s@gmail.com"),
]

struct PrivacyView: View {
    let onBack: () -> Void

    var body: some View {
        LegalScreenShell(title: "Kebijakan Privasi", onBack: onBack) {
            Text("Berlaku sejak 1 Januari 2026 · Diperbarui 5 Mei 2026")
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 16)

            LegalSectionTitle(text: "Ringkasan")

            VStack(alignment: .leading, spacing: 4) {
                ForEach(summaryBullets, id: \.self) { line in
                    Text("•  \(line)")
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextPrimary)
                        .lineSpacing(4)
                        .fixedSize(horizontal: false, vertical: true)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(14)
            .background(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(Color.sgPrimarySofter)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .strokeBorder(Color.sgPrimary.opacity(0.18), lineWidth: 1)
            )

            VStack(alignment: .leading, spacing: 0) {
                ForEach(sections, id: \.0) { section in
                    LegalParagraph(heading: section.0, text: section.1)
                }
            }
            .padding(.top, 16)
        }
    }
}

#Preview {
    PrivacyView(onBack: {})
}
