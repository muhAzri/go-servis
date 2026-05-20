package com.zrifapps.goservice.ui.tips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private data class RelatedTip(val title: String, val readTime: String)

private val relatedTips = listOf(
    RelatedTip("Cara cek oli motor manual", "3 menit"),
    RelatedTip("Beda oli mineral vs sintetik", "4 menit"),
)

@Composable
fun TipsDetailScreen(
    onBack: () -> Unit,
    onOpenAddService: () -> Unit = {},
    onOpenRelated: (String) -> Unit = {},
) {
    var bookmarked by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(
            onBack = onBack,
            bookmarked = bookmarked,
            onToggleBookmark = { bookmarked = !bookmarked },
            onShare = {},
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HeroImage()
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CategoryChip(label = "OLI", icon = FaIcons.OIL_CAN, color = Color(0xFFE89C2E))
                Text(
                    text = "·",
                    color = AppColors.TextSubtle,
                    fontSize = 13.sp,
                )
                Text(
                    text = "2 menit baca",
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = plusJakartaSansFontFamily(),
                )
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Kapan harus ganti oli motor matic?",
                color = AppColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                lineHeight = 32.sp,
                fontFamily = plusJakartaSansFontFamily(),
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Dipublikasi 5 Mei 2026",
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = plusJakartaSansFontFamily(),
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppColors.Border),
            )
            Spacer(Modifier.height(16.dp))

            Body()

            Spacer(Modifier.height(20.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ContextBanner(
                    title = "Rekomendasi untuk Beat Hitam",
                    body = "Beat Hitam butuh ganti oli dalam 580 km. Catat sekarang supaya tepat waktu.",
                    icon = FaIcons.LIGHTBULB,
                    tone = ContextBannerTone.Info,
                    ctaLabel = "Catat servis sekarang →",
                    onCta = onOpenAddService,
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "TIPS TERKAIT",
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                fontFamily = plusJakartaSansFontFamily(),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 8.dp),
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                relatedTips.forEach { tip ->
                    RelatedRow(tip = tip, onClick = { onOpenRelated(tip.title) })
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    bookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircleIconButton(
                icon = FaIcons.STAR,
                onClick = onToggleBookmark,
                iconColor = if (bookmarked) AppColors.Warning else AppColors.TextPrimary,
            )
            CircleIconButton(icon = FaIcons.SHARE, onClick = onShare)
        }
    }
}

@Composable
private fun HeroImage() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        AppColors.PrimarySoft,
                        AppColors.WarningSoft,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "[ tip cover · 16:9 ]",
            color = AppColors.TextSubtle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun CategoryChip(label: String, icon: String, color: Color) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FaIcon(icon = icon, color = color, size = 11.sp)
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            fontFamily = font,
        )
    }
}

@Composable
private fun Body() {
    val font = plusJakartaSansFontFamily()
    val paragraphs = listOf(
        "Oli matic punya peran ganda — melumasi mesin dan mendinginkan CVT. Karena dua tugas itu, intervalnya lebih pendek dibanding motor manual: 2.000 km atau 2 bulan, mana yang tercapai lebih dulu.",
        "Kalau motor sering kena macet ibu kota, hitung interval real-nya: 1 jam macet kira-kira setara 25 km perjalanan biasa. Beat yang sering nganterin anak sekolah pagi-pagi bisa butuh ganti oli sekitar 1.500 km — lebih awal dari rekomendasi pabrik.",
        "Cara cek sendiri: tarik dipstick (kalau matic Honda ada di sisi kanan mesin), lap dengan kain, lalu cocokin lagi. Oli yang masih bagus warnanya kuning kecokelatan dan tidak ada bau bensin. Kalau warnanya hitam pekat atau mengandung partikel berkilau, segera ganti.",
    )
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        paragraphs.forEach {
            Text(
                text = it,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
private fun RelatedRow(tip: RelatedTip, onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FaIcon(icon = FaIcons.LIGHTBULB, color = AppColors.Warning, size = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tip.title,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = tip.readTime,
                color = AppColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 12.sp)
    }
}
