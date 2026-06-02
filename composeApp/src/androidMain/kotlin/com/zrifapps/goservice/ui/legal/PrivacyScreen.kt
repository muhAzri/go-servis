package com.zrifapps.goservice.ui.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors

private val SUMMARY_BULLETS = listOf(
    "Data kendaraan, servis, pengingat, komponen, dan profil disimpan lokal di HP kamu",
    "Tidak ada login, tidak ada akun, dan tidak ada sync data ke server kami",
    "Iklan ditampilkan via SDK Yandex Mobile Ads (mediation pihak ketiga)",
    "File backup CSV hanya keluar dari perangkat kalau kamu sendiri yang mengekspor",
)

private val SECTIONS = listOf(
    "1. Data yang kami simpan (semua lokal)" to "Aplikasi ini bersifat lokal-first. Berikut data yang kami simpan di penyimpanan privat aplikasi (SQLite) di perangkatmu:\n" +
        "• Kendaraan: nama panggilan, merek, model, tahun, plat nomor, warna, dan odometer.\n" +
        "• Riwayat servis: mode (Komponen/Rutin/Manual), tanggal, odometer, bengkel, biaya, catatan, dan komponen terkait.\n" +
        "• Komponen yang dipantau: nama, interval, tanggal & KM servis terakhir, jadwal servis berikutnya, badge urgensi.\n" +
        "• Pengingat: judul, target KM dan/atau tanggal, status (aktif, ditunda, selesai, diabaikan).\n" +
        "• Profil: nama, warna avatar, dan email (opsional).\n" +
        "• Pengaturan: toggle notifikasi dan preferensi terkait.\n\n" +
        "Data ini tidak dikirim ke server kami dan tidak ada akun online yang kamu buat. Aplikasi lain di perangkat tidak bisa mengakses penyimpanan privat ini.",
    "2. Notifikasi & Izin" to "Notifikasi sepenuhnya dijadwalkan dan dipicu secara lokal — kami tidak menggunakan push server.\n\n" +
        "Aplikasi punya dua kategori notifikasi yang bisa kamu atur terpisah:\n" +
        "• Pengingat servis: muncul menjelang jatuh tempo pengingat yang kamu buat.\n" +
        "• Update KM: otomatis mengingatkanmu kalau odometer salah satu kendaraan tidak diperbarui selama 14 hari, jam 9 pagi waktu lokal. Tap notif membuka layar Update KM kendaraan tersebut.\n\n" +
        "Di Android 13+ aplikasi akan meminta izin POST_NOTIFICATIONS saat onboarding atau saat kamu menggunakan tombol tes di Pengaturan. Kamu bisa mematikan masing-masing kategori di Pengaturan > Notifikasi atau di setelan notifikasi sistem.",
    "3. Iklan & Pihak Ketiga" to "Saat ini kami menampilkan iklan via SDK Yandex Mobile Ads (banner, interstitial, native, app-open). SDK ini dapat mengakses identifier iklan perangkat (AAID di Android / IDFA di iOS) untuk personalisasi dan measurement sesuai kebijakan Yandex.\n\n" +
        "Kamu bisa mereset atau membatasi iklan terpersonalisasi via setelan sistem (Android: Setelan > Privasi > Iklan; iOS: Setelan > Privasi & Keamanan > Pelacakan). Kami tidak menambahkan SDK analitik atau pelacakan lintas-aplikasi lain di atas SDK iklan ini.",
    "4. Backup & Ekspor Data" to "Fitur Ekspor di Pengaturan menghasilkan satu file CSV yang berisi data kendaraan, servis, pengingat, dan komponen yang kamu pilih. File ini hanya keluar dari perangkat kalau kamu sendiri yang menekan Share / Save lewat dialog sistem.\n\n" +
        "Kami tidak menyimpan salinan file ekspor di server manapun. Setelah file keluar dari perangkat (mis. dibagikan via WhatsApp, email, atau cloud drive), keamanannya menjadi tanggung jawab kamu — file dapat memuat informasi yang bisa mengidentifikasi kendaraanmu (plat nomor, riwayat bengkel, biaya).\n\n" +
        "Fitur Impor hanya membaca file CSV yang kamu pilih sendiri; impor melakukan merge by id sehingga re-impor tidak menggandakan data.",
    "5. Penyimpanan Lokal & Penghapusan Data" to "Semua data hidup di app-private storage milik ServisGo:\n" +
        "• 'Hapus Semua Data' di Pengaturan akan menghapus seluruh data aplikasi di perangkat ini.\n" +
        "• Uninstall aplikasi juga menghapus seluruh data lokal secara permanen.\n\n" +
        "Karena kami tidak punya backup di server kami, data yang sudah dihapus atau hilang akibat uninstall/format perangkat tidak dapat kami pulihkan. Disarankan untuk mengekspor CSV berkala (lihat bagian 4).",
    "6. Anak di Bawah Umur" to "Aplikasi ini tidak ditujukan untuk pengguna di bawah 13 tahun. Kami tidak sengaja mengumpulkan data dari anak-anak. Karena data tinggal di perangkat, jika orang tua/wali menemukan data anak di perangkatnya, dapat menghapusnya melalui 'Hapus Semua Data' atau dengan menguninstall aplikasi.",
    "7. Perubahan Kebijakan" to "Kami dapat memperbarui kebijakan ini sesekali untuk mencerminkan fitur baru atau perubahan SDK pihak ketiga. Tanggal 'Diperbarui' di bagian atas layar ini akan kami ubah setiap kali ada revisi, dan perubahan signifikan pada praktik data akan kami sorot pada catatan rilis aplikasi.",
    "8. Kontak" to "Pertanyaan, keluhan, atau permintaan terkait privasi: muhammad.azri.f.s@gmail.com",
)

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    LegalShell(title = "Kebijakan Privasi", onBack = onBack) {
        Text(
            text = "Berlaku sejak 1 Januari 2026 · Diperbarui 2 Juni 2026",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LegalSectionTitle("Ringkasan")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, AppColors.Primary.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                .background(AppColors.PrimarySofter)
                .padding(14.dp),
        ) {
            SUMMARY_BULLETS.forEach { line ->
                Text(
                    text = "•  $line",
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }

        Column(modifier = Modifier.padding(top = 16.dp)) {
            SECTIONS.forEach { (heading, body) ->
                LegalParagraph(heading = heading, text = body)
            }
        }
    }
}
