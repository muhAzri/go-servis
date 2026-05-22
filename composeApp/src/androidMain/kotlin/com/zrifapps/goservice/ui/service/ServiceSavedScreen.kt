package com.zrifapps.goservice.ui.service

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun ServiceSavedScreen(
    onBackToHome: () -> Unit,
    onOpenHistory: () -> Unit = {},
    onOpenServiceDetail: () -> Unit = {},
    onAddReminderFromContext: () -> Unit = {},
) {
    val font = plusJakartaSansFontFamily()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SuccessIcon()
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Servis tercatat 🎉",
                color = AppColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                fontFamily = font,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Pengingat berikutnya: ganti oli pada",
                color = AppColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "20.420 km / 6 Juli 2026",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))
            SavedSummaryCard(onClick = onOpenServiceDetail)

            Spacer(Modifier.height(16.dp))
            NextReminderCtaCard(onCustomize = onAddReminderFromContext)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppButton(text = "Kembali ke Beranda", onClick = onBackToHome)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clickable(onClick = onOpenServiceDetail),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Lihat Detail",
                        color = AppColors.TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clickable(onClick = onOpenHistory),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Lihat Riwayat",
                        color = AppColors.TextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                    )
                }
            }
        }
    }
}

private enum class NextReminderState { Cta, Animating, Saved }

@Composable
private fun NextReminderCtaCard(onCustomize: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    var state by remember { mutableStateOf(NextReminderState.Cta) }

    LaunchedEffect(state) {
        if (state == NextReminderState.Animating) {
            kotlinx.coroutines.delay(400)
            state = NextReminderState.Saved
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.PrimarySofter)
            .border(BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.2f)), RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.BELL, color = Color.White, size = 16.sp)
            }
            Spacer(Modifier.size(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pengingat berikutnya",
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
                Text(
                    text = "20.420 km · 6 Juli 2026",
                    color = AppColors.TextMuted,
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        when (state) {
            NextReminderState.Cta -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.Primary)
                            .clickable { state = NextReminderState.Animating },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Set otomatis",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(BorderStroke(1.5.dp, AppColors.Primary), RoundedCornerShape(10.dp))
                            .clickable(onClick = onCustomize),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Ubah dulu",
                            color = AppColors.Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                        )
                    }
                }
            }
            NextReminderState.Animating -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.PrimarySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = AppColors.Primary,
                        strokeWidth = 2.5.dp,
                    )
                }
            }
            NextReminderState.Saved -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.PrimarySoft)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FaIcon(icon = FaIcons.CHECK, color = AppColors.Primary, size = 14.sp)
                    Text(
                        text = "Reminder dibuat: 5.000 km / 6 bulan",
                        color = AppColors.Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(96.dp)
            .shadow(elevation = 20.dp, shape = CircleShape, spotColor = AppColors.Primary)
            .clip(CircleShape)
            .background(AppColors.Primary),
        contentAlignment = Alignment.Center,
    ) {
        FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 44.sp)
    }
}

@Composable
private fun SavedSummaryCard(onClick: () -> Unit = {}) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        IconBadge(
            icon = FaIcons.OIL_CAN,
            foreground = AppColors.Warning,
            background = AppColors.WarningSoft,
            size = 36.dp, iconSize = 18.sp, corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ganti Oli Mesin",
                color = AppColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "Beat Hitam · Rp 65.000",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
    }
}
