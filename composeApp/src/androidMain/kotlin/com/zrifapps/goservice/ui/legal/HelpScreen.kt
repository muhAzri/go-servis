package com.zrifapps.goservice.ui.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons

private val FAQ = listOf(
    "Bagaimana cara menambah kendaraan?" to "Tap tombol + di tab tengah, lalu pilih jenis (motor/mobil), isi merk, model, plat, dan KM saat ini. Selesai.",
    "Apakah saya perlu update KM tiap hari?" to "Tidak. Update saat kamu ingat saja — minimal sebulan sekali agar pengingat akurat.",
    "Data saya hilang setelah uninstall?" to "Iya, karena data disimpan lokal di perangkat dan kami tidak menyimpan ke server. Pastikan ekspor CSV sebelum uninstall jika ingin menyimpan riwayat.",
    "Notifikasi tidak muncul?" to "Pastikan izin notifikasi aktif di Pengaturan HP > ServisGo > Notifikasi.",
    "Apakah ServisGo gratis?" to "Ya, gratis dengan iklan. Kami menjaga privasimu — tidak ada login, tidak ada tracking lintas aplikasi dari kami.",
)

@Composable
fun HelpScreen(onBack: () -> Unit) {
    LegalShell(title = "Bantuan & FAQ", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp),
        ) {
            FAQ.forEachIndexed { index, (q, a) ->
                FaqRow(question = q, answer = a)
                if (index < FAQ.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppColors.Border),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, AppColors.Primary.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                .background(AppColors.PrimarySofter)
                .padding(14.dp),
        ) {
            Text(
                text = "Masih butuh bantuan?",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Email kami di muhammad.azri.f.s@gmail.com — biasanya dijawab dalam 1×24 jam.",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun FaqRow(question: String, answer: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        FaIcon(icon = FaIcons.CIRCLE_INFO, color = AppColors.Primary, size = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = question,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = answer,
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }
}
