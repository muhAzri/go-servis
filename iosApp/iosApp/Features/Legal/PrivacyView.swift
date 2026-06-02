import SwiftUI

private let summaryBullets = [
    "Data kendaraan, servis, pengingat, komponen, dan profil disimpan lokal di HP kamu",
    "Tidak ada login, tidak ada akun, dan tidak ada sync data ke server kami",
    "Iklan ditampilkan via SDK Yandex Mobile Ads (mediation pihak ketiga)",
    "File backup CSV hanya keluar dari perangkat kalau kamu sendiri yang mengekspor",
]

private let sections: [(String, String)] = [
    ("1. Data yang kami simpan (semua lokal)",
     "Aplikasi ini bersifat lokal-first. Berikut data yang kami simpan di penyimpanan privat aplikasi (SQLite) di perangkatmu:\n" +
     "• Kendaraan: nama panggilan, merek, model, tahun, plat nomor, warna, dan odometer.\n" +
     "• Riwayat servis: mode (Komponen/Rutin/Manual), tanggal, odometer, bengkel, biaya, catatan, dan komponen terkait.\n" +
     "• Komponen yang dipantau: nama, interval, tanggal & KM servis terakhir, jadwal servis berikutnya, badge urgensi.\n" +
     "• Pengingat: judul, target KM dan/atau tanggal, status (aktif, ditunda, selesai, diabaikan).\n" +
     "• Profil: nama, warna avatar, dan email (opsional).\n" +
     "• Pengaturan: toggle notifikasi dan preferensi terkait.\n\n" +
     "Data ini tidak dikirim ke server kami dan tidak ada akun online yang kamu buat. Aplikasi lain di perangkat tidak bisa mengakses penyimpanan privat ini."),
    ("2. Notifikasi & Izin",
     "Notifikasi sepenuhnya dijadwalkan dan dipicu secara lokal — kami tidak menggunakan push server.\n\n" +
     "Aplikasi punya dua kategori notifikasi yang bisa kamu atur terpisah:\n" +
     "• Pengingat servis: muncul menjelang jatuh tempo pengingat yang kamu buat.\n" +
     "• Update KM: otomatis mengingatkanmu kalau odometer salah satu kendaraan tidak diperbarui selama 14 hari, jam 9 pagi waktu lokal. Tap notif membuka layar Update KM kendaraan tersebut.\n\n" +
     "Di iOS, aplikasi akan meminta izin notifikasi saat onboarding atau saat kamu menggunakan tombol tes di Pengaturan. Di Android 13+, izin POST_NOTIFICATIONS diminta pada momen yang sama. Kamu bisa mematikan masing-masing kategori di Pengaturan > Notifikasi atau di setelan notifikasi sistem."),
    ("3. Iklan & Pihak Ketiga",
     "Saat ini kami menampilkan iklan via SDK Yandex Mobile Ads (banner, interstitial, native, app-open). SDK ini dapat mengakses identifier iklan perangkat (IDFA di iOS / AAID di Android) untuk personalisasi dan measurement sesuai kebijakan Yandex.\n\n" +
     "Kamu bisa mereset atau membatasi iklan terpersonalisasi via setelan sistem (iOS: Setelan > Privasi & Keamanan > Pelacakan; Android: Setelan > Privasi > Iklan). Kami tidak menambahkan SDK analitik atau pelacakan lintas-aplikasi lain di atas SDK iklan ini."),
    ("4. Backup & Ekspor Data",
     "Fitur Ekspor di Pengaturan menghasilkan satu file CSV yang berisi data kendaraan, servis, pengingat, dan komponen yang kamu pilih. File ini hanya keluar dari perangkat kalau kamu sendiri yang menekan Share / Save lewat dialog sistem.\n\n" +
     "Kami tidak menyimpan salinan file ekspor di server manapun. Setelah file keluar dari perangkat (mis. dibagikan via WhatsApp, email, atau iCloud Drive), keamanannya menjadi tanggung jawab kamu — file dapat memuat informasi yang bisa mengidentifikasi kendaraanmu (plat nomor, riwayat bengkel, biaya).\n\n" +
     "Fitur Impor hanya membaca file CSV yang kamu pilih sendiri; impor melakukan merge by id sehingga re-impor tidak menggandakan data."),
    ("5. Penyimpanan Lokal & Penghapusan Data",
     "Semua data hidup di app-private storage milik ServisGo:\n" +
     "• 'Hapus Semua Data' di Pengaturan akan menghapus seluruh data aplikasi di perangkat ini.\n" +
     "• Uninstall aplikasi juga menghapus seluruh data lokal secara permanen.\n\n" +
     "Karena kami tidak punya backup di server kami, data yang sudah dihapus atau hilang akibat uninstall/format perangkat tidak dapat kami pulihkan. Disarankan untuk mengekspor CSV berkala (lihat bagian 4)."),
    ("6. Anak di Bawah Umur", "Aplikasi ini tidak ditujukan untuk pengguna di bawah 13 tahun. Kami tidak sengaja mengumpulkan data dari anak-anak. Karena data tinggal di perangkat, jika orang tua/wali menemukan data anak di perangkatnya, dapat menghapusnya melalui 'Hapus Semua Data' atau dengan menguninstall aplikasi."),
    ("7. Perubahan Kebijakan", "Kami dapat memperbarui kebijakan ini sesekali untuk mencerminkan fitur baru atau perubahan SDK pihak ketiga. Tanggal 'Diperbarui' di bagian atas layar ini akan kami ubah setiap kali ada revisi, dan perubahan signifikan pada praktik data akan kami sorot pada catatan rilis aplikasi."),
    ("8. Kontak", "Pertanyaan, keluhan, atau permintaan terkait privasi: muhammad.azri.f.s@gmail.com"),
]

struct PrivacyView: View {
    let onBack: () -> Void

    var body: some View {
        LegalScreenShell(title: "Kebijakan Privasi", onBack: onBack) {
            Text("Berlaku sejak 1 Januari 2026 · Diperbarui 2 Juni 2026")
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
