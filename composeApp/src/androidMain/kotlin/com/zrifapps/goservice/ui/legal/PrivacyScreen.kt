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
    "Data kendaraan & servis disimpan lokal di HP kamu",
    "Tidak ada login, tidak ada akun",
    "Iklan ditampilkan via jaringan ad mediation pihak ketiga",
    "Tidak ada pelacakan lintas aplikasi dari kami",
)

private val SECTIONS = listOf(
    "1. Data yang kami kumpulkan" to "Aplikasi ini bersifat lokal-first. Data kendaraan, KM, riwayat servis, dan pengingat disimpan di penyimpanan lokal perangkat. Kami tidak mengirim data tersebut ke server kami.",
    "2. Iklan & Pihak Ketiga" to "Kami menggunakan jaringan iklan dengan mediation untuk monetisasi (beberapa SDK pihak ketiga seperti AdMob, Yandex Ads, dan jaringan lain dapat ikut serta dalam mediation). Jaringan ini dapat mengakses identifier iklan perangkat (AAID/IDFA) sesuai kebijakan masing-masing. Kamu bisa mereset atau menonaktifkan personalisasi iklan via pengaturan sistem.",
    "3. Notifikasi" to "Notifikasi pengingat servis dijalankan secara lokal — tidak melalui server. Kamu bisa mengatur atau menonaktifkan kapan saja di Pengaturan > Notifikasi.",
    "4. Anak di Bawah Umur" to "Aplikasi ini tidak ditujukan untuk pengguna di bawah 13 tahun. Kami tidak sengaja mengumpulkan data dari anak-anak.",
    "5. Perubahan Kebijakan" to "Kami dapat memperbarui kebijakan ini sesekali. Kamu akan diberitahu melalui notifikasi in-app jika ada perubahan signifikan.",
    "6. Kontak" to "Pertanyaan privasi: muhammad.azri.f.s@gmail.com",
)

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    LegalShell(title = "Kebijakan Privasi", onBack = onBack) {
        Text(
            text = "Berlaku sejak 1 Januari 2026 · Diperbarui 5 Mei 2026",
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
                LegalParagraph(heading = heading, body = body)
            }
        }
    }
}
