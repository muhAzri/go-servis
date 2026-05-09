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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons

@Composable
fun AboutScreen(onBack: () -> Unit) {
    LegalShell(title = "Tentang ServisGo", onBack = onBack) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = AppColors.Primary,
                        spotColor = AppColors.Primary,
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.WRENCH, color = Color.White, size = 38.sp)
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "ServisGo",
                color = AppColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "v1.0.0 · build 2026.05.06",
                color = AppColors.TextMuted,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Pengingat servis sederhana untuk pemilik motor & mobil di Indonesia. Catat sekali, lupa-lupa nanti — kami yang ingatkan.",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, AppColors.Border, RoundedCornerShape(18.dp))
                .background(AppColors.Surface)
                .padding(horizontal = 16.dp),
        ) {
            InfoRow(label = "Pengembang", value = "Muhammad Azri Fatihah Susanto (Personal)", showDivider = true)
            InfoRow(label = "Email", value = "muhammad.azri.f.s@gmail.com", showDivider = false)
        }

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedActionButton(text = "★ Beri Rating", modifier = Modifier.weight(1f))
            OutlinedActionButton(text = "↗ Bagikan App", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, showDivider: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = AppColors.TextMuted, fontSize = 13.sp)
        Text(
            text = value,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
    if (showDivider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColors.Border),
        )
    }
}

@Composable
private fun OutlinedActionButton(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp))
            .background(AppColors.Surface),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = AppColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
