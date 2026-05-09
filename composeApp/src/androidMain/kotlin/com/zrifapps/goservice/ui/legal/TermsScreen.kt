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
    "2. Penggunaan Aplikasi" to "Aplikasi disediakan \"apa adanya\" untuk membantu kamu mengingat jadwal servis kendaraan. ServisGo adalah alat bantu pengingat — keputusan kapan dan di mana servis tetap menjadi tanggung jawab kamu.",
    "3. Tidak Ada Jaminan" to "Kami tidak menjamin keakuratan rekomendasi interval servis. Selalu rujuk buku manual pabrikan dan konsultasi mekanik bersertifikat untuk panduan resmi.",
    "4. Iklan & Konten Pihak Ketiga" to "Aplikasi menampilkan iklan dari jaringan ad mediation (AdMob, Yandex Ads, dan jaringan lain). Kami tidak bertanggung jawab atas konten, produk, atau layanan yang ditampilkan dalam iklan tersebut.",
    "5. Hak Kekayaan Intelektual" to "Logo, desain, dan kode aplikasi adalah milik pengembang ServisGo. Konten yang kamu masukkan (data kendaraan, catatan) tetap milikmu.",
    "6. Pembatasan Tanggung Jawab" to "Kami tidak bertanggung jawab atas kerugian yang timbul dari kegagalan komponen kendaraan, kelupaan servis, atau ketidakakuratan data yang kamu masukkan.",
    "7. Penghentian Layanan" to "Kami berhak menghentikan atau memodifikasi layanan kapan saja dengan pemberitahuan yang wajar.",
    "8. Hukum yang Berlaku" to "Syarat ini diatur oleh hukum Republik Indonesia.",
    "9. Kontak" to "muhammad.azri.f.s@gmail.com",
)

@Composable
fun TermsScreen(onBack: () -> Unit) {
    LegalShell(title = "Syarat & Ketentuan", onBack = onBack) {
        Text(
            text = "Berlaku sejak 1 Januari 2026",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        SECTIONS.forEach { (heading, body) ->
            LegalParagraph(heading = heading, text = body)
        }
    }
}
