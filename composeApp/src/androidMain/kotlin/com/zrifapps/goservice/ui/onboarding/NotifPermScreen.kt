package com.zrifapps.goservice.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun NotifPermScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()

    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onComplete() },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            FaIcon(icon = FaIcons.ARROW_LEFT, color = AppColors.TextPrimary, size = 18.sp)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Langkah 3 dari 3",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            color = AppColors.TextMuted,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Aktifkan pengingat",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            color = AppColors.TextPrimary,
            lineHeight = 32.sp,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Kami kirim notif saat servis hampir tiba — supaya tidak kelupaan.",
            fontSize = 14.sp,
            fontFamily = font,
            color = AppColors.TextMuted,
            lineHeight = 21.sp,
        )

        Spacer(Modifier.height(24.dp))

        // Mock notification card 1 — overdue
        NotifPreviewCard(
            accentColor = AppColors.Danger,
            iconUnicode = FaIcons.WRENCH,
            title = "Beat Hitam — Ganti oli telat 16 hari",
            subtitle = "Sudah 18.420 km, target 18.000 km",
            time = "sekarang",
            alpha = 1f,
            font = font,
        )

        Spacer(Modifier.height(8.dp))

        // Mock notification card 2 — upcoming
        NotifPreviewCard(
            accentColor = AppColors.Warning,
            iconUnicode = FaIcons.BELL,
            title = "Brio Biru — Servis berkala 3 hari lagi",
            subtitle = "9 Mei 2026 · Auto2000 Cikarang",
            time = "3j yang lalu",
            alpha = 0.85f,
            font = font,
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
        ) {
            Text(
                text = "Aktifkan Notifikasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                color = Color.White,
            )
        }

        Spacer(Modifier.height(4.dp))

        TextButton(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = "Nanti saja",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                color = AppColors.TextMuted,
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Mimics Android 12+ (Material You) notification style:
 * - Small 28dp coloured circle with monochrome white icon in the header row
 * - Header: [small icon] AppName · time — all small grey text
 * - Content block below: bold title + regular body
 */
@Composable
private fun NotifPreviewCard(
    accentColor: Color,
    iconUnicode: String,
    title: String,
    subtitle: String,
    time: String,
    alpha: Float,
    font: androidx.compose.ui.text.font.FontFamily,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Surface.copy(alpha = alpha),
        ),
    ) {
        Column(
            modifier = Modifier.padding(
                start = 14.dp, end = 14.dp, top = 10.dp, bottom = 12.dp,
            ),
        ) {
            // ── Header row (Android-style: small icon + app name + dot + time) ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Small notification icon — 28dp circle, coloured bg, white icon inside
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center,
                ) {
                    FaIcon(icon = iconUnicode, color = Color.White, size = 10.sp)
                }
                Text(
                    text = "ServisGo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    color = AppColors.TextMuted,
                )
                Text(
                    text = "·",
                    fontSize = 11.sp,
                    fontFamily = font,
                    color = AppColors.TextMuted,
                )
                Text(
                    text = time,
                    fontSize = 11.sp,
                    fontFamily = font,
                    color = AppColors.TextMuted,
                )
                Spacer(Modifier.weight(1f))
                // Expand chevron — common in Android notifications
                FaIcon(
                    icon = FaIcons.CHEVRON_DOWN,
                    color = AppColors.TextSubtle,
                    size = 10.sp,
                )
            }

            Spacer(Modifier.height(6.dp))

            // ── Content ──
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                color = AppColors.TextPrimary,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontFamily = font,
                color = AppColors.TextMuted,
                lineHeight = 16.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotifPermScreenPreview() {
    NotifPermScreen(onBack = {}, onComplete = {})
}
