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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private data class TipEntry(val tag: String, val title: String, val readTime: String)

private val tips = listOf(
    TipEntry("Oli",    "Kapan harus ganti oli motor matic?",    "2 menit"),
    TipEntry("Ban",    "Cara cek tekanan ban yang benar",        "3 menit"),
    TipEntry("Aki",    "Tanda-tanda aki mobil mau soak",         "4 menit"),
    TipEntry("Tips",   "5 hal sebelum mudik dengan motor",       "5 menit"),
    TipEntry("Servis", "Beda servis berkala 1.000 vs 5.000 km",  "3 menit"),
)

@Composable
fun TipsScreen(
    onBack: () -> Unit,
    onOpenTipDetail: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState()),
    ) {
        TipsTopBar(onBack = onBack)
        HeroTip(onClick = onOpenTipDetail)
        Spacer(Modifier.height(14.dp))
        AdBannerSlot()
        Spacer(Modifier.height(14.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tips.forEach { TipRow(it, onClick = onOpenTipDetail) }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun TipsTopBar(onBack: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Tips Perawatan",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun HeroTip(onClick: () -> Unit = {}) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(listOf(AppColors.Primary, AppColors.PrimaryDark)),
            )
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        Text(
            text = "TIP HARI INI",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            fontFamily = font,
        )
        Text(
            text = "Cek tekanan ban tiap 2 minggu — irit BBM hingga 7%",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            lineHeight = 28.sp,
            fontFamily = font,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = "3 menit baca · oleh Tim ServisGo",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun TipRow(tip: TipEntry, onClick: () -> Unit = {}) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.SurfaceAlt),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.LIGHTBULB, color = AppColors.TextMuted, size = 26.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppColors.PrimarySoft)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = tip.tag.uppercase(),
                    color = AppColors.Primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontFamily = font,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = tip.title,
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                lineHeight = 17.sp,
            )
            Text(
                text = tip.readTime,
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
