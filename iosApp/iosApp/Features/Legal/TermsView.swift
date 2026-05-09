import SwiftUI

private let sections: [(String, String)] = [
    ("1. Penerimaan Syarat", "Dengan mengunduh dan menggunakan aplikasi ServisGo (\"Aplikasi\"), kamu setuju untuk terikat oleh syarat dan ketentuan berikut. Jika kamu tidak setuju, mohon tidak menggunakan Aplikasi."),
    ("2. Penggunaan Aplikasi", "Aplikasi disediakan \"apa adanya\" untuk membantu kamu mengingat jadwal servis kendaraan. ServisGo adalah alat bantu pengingat — keputusan kapan dan di mana servis tetap menjadi tanggung jawab kamu."),
    ("3. Tidak Ada Jaminan", "Kami tidak menjamin keakuratan rekomendasi interval servis. Selalu rujuk buku manual pabrikan dan konsultasi mekanik bersertifikat untuk panduan resmi."),
    ("4. Iklan & Konten Pihak Ketiga", "Aplikasi menampilkan iklan dari jaringan ad mediation (AdMob, Yandex Ads, dan jaringan lain). Kami tidak bertanggung jawab atas konten, produk, atau layanan yang ditampilkan dalam iklan tersebut."),
    ("5. Hak Kekayaan Intelektual", "Logo, desain, dan kode aplikasi adalah milik pengembang ServisGo. Konten yang kamu masukkan (data kendaraan, catatan) tetap milikmu."),
    ("6. Pembatasan Tanggung Jawab", "Kami tidak bertanggung jawab atas kerugian yang timbul dari kegagalan komponen kendaraan, kelupaan servis, atau ketidakakuratan data yang kamu masukkan."),
    ("7. Penghentian Layanan", "Kami berhak menghentikan atau memodifikasi layanan kapan saja dengan pemberitahuan yang wajar."),
    ("8. Hukum yang Berlaku", "Syarat ini diatur oleh hukum Republik Indonesia."),
    ("9. Kontak", "muhammad.azri.f.s@gmail.com"),
]

struct TermsView: View {
    let onBack: () -> Void

    var body: some View {
        LegalScreenShell(title: "Syarat & Ketentuan", onBack: onBack) {
            Text("Berlaku sejak 1 Januari 2026")
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 16)

            ForEach(sections, id: \.0) { section in
                LegalParagraph(heading: section.0, text: section.1)
            }
        }
    }
}

#Preview {
    TermsView(onBack: {})
}
