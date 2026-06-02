package com.zrifapps.goservice.ui.legal

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors

private val SECTIONS = listOf(
    "1. Penerimaan Syarat" to "Dengan mengunduh dan menggunakan aplikasi ServisGo (\"Aplikasi\"), kamu setuju untuk terikat oleh syarat dan ketentuan berikut. Jika kamu tidak setuju, mohon tidak menggunakan Aplikasi.",
    "2. Penggunaan Aplikasi" to "ServisGo adalah alat bantu untuk mencatat servis kendaraan, melacak komponen yang dipantau, dan mengingatkan jadwal servis. Fitur utama yang tersedia saat ini:\n" +
        "• Catat servis dalam tiga mode (Komponen / Rutin / Manual).\n" +
        "• Pengingat berbasis komponen yang dipantau atau pengingat manual.\n" +
        "• Pengingat otomatis Update KM bila odometer tidak diperbarui dalam 14 hari.\n" +
        "• Ekspor dan impor data dalam format CSV.\n\n" +
        "Aplikasi disediakan \"apa adanya\". ServisGo bukan pengganti panduan resmi pabrikan kendaraan atau penilaian mekanik bersertifikat — keputusan kapan, di mana, dan bagaimana servis dilakukan tetap menjadi tanggung jawab kamu.",
    "3. Tidak Ada Jaminan" to "Kami tidak menjamin keakuratan rekomendasi interval servis bawaan, badge urgensi komponen, atau ambang batas 14 hari pada pengingat Update KM. Angka-angka tersebut merupakan default umum, bukan rekomendasi resmi untuk kendaraan spesifikmu. Selalu rujuk buku manual pabrikan dan konsultasi mekanik bersertifikat untuk panduan resmi.",
    "4. Iklan & Konten Pihak Ketiga" to "Saat ini Aplikasi menampilkan iklan melalui SDK Yandex Mobile Ads. Komposisi SDK iklan dapat berubah seiring waktu seiring kebutuhan monetisasi. Kami tidak bertanggung jawab atas konten, produk, atau layanan yang ditampilkan dalam iklan tersebut. Iklan dapat dimuat dari jaringan dan menggunakan identifier perangkat sesuai kebijakan SDK iklan yang berlaku — lihat Kebijakan Privasi untuk detailnya.",
    "5. Data Lokal & Tanggung Jawab Backup" to "Semua data Aplikasi disimpan di perangkatmu. Kami TIDAK menyediakan sync cloud atau backup di server kami. Konsekuensinya:\n" +
        "• Uninstall, factory reset, kerusakan storage, atau penggantian perangkat dapat menyebabkan hilangnya seluruh data Aplikasi secara permanen.\n" +
        "• Kami tidak dapat memulihkan data yang telah hilang dengan cara apapun.\n\n" +
        "Sangat disarankan untuk menggunakan fitur Ekspor CSV secara berkala dan menyimpan file hasil ekspor di lokasi terpisah yang kamu kendalikan (mis. cloud drive pribadi).",
    "6. Backup, Ekspor, dan Impor Data" to "Fitur Ekspor menghasilkan satu file CSV yang berisi data Aplikasi yang kamu pilih. Setelah file keluar dari Aplikasi via dialog share/save sistem, kamu sepenuhnya bertanggung jawab atas tempat ia disimpan dan kepada siapa ia dibagikan — file dapat memuat informasi yang bisa mengidentifikasi kendaraanmu.\n\n" +
        "Fitur Impor hanya menerima file CSV yang dihasilkan oleh Aplikasi ini. File dengan format tidak dikenali akan ditolak. Proses impor melakukan merge berdasarkan id: data yang sudah ada tidak akan digandakan, tapi data dengan id yang sama dari file impor dapat menimpa versi lokal yang lebih lama.",
    "7. Hak Kekayaan Intelektual" to "Logo, desain, dan kode aplikasi adalah milik pengembang ServisGo. Konten yang kamu masukkan (data kendaraan, catatan) tetap milikmu.",
    "8. Pembatasan Tanggung Jawab" to "Kami tidak bertanggung jawab atas kerugian, langsung maupun tidak langsung, yang timbul dari: kegagalan komponen kendaraan, kelupaan jadwal servis, ketidakakuratan data yang kamu masukkan, kehilangan data lokal, atau kebocoran data yang terjadi setelah kamu mengekspor file backup ke luar perangkat.",
    "9. Penghentian Layanan" to "Kami berhak menghentikan atau memodifikasi layanan kapan saja dengan pemberitahuan yang wajar. Karena tidak ada data Aplikasi yang kami simpan di server, penghentian layanan tidak memengaruhi data lokal yang ada di perangkatmu.",
    "10. Hukum yang Berlaku" to "Syarat ini diatur oleh hukum Republik Indonesia.",
    "11. Kontak" to "muhammad.azri.f.s@gmail.com",
)

@Composable
fun TermsScreen(onBack: () -> Unit) {
    LegalShell(title = "Syarat & Ketentuan", onBack = onBack) {
        Text(
            text = "Berlaku sejak 1 Januari 2026 · Diperbarui 2 Juni 2026",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        SECTIONS.forEach { (heading, body) ->
            LegalParagraph(heading = heading, text = body)
        }
    }
}
