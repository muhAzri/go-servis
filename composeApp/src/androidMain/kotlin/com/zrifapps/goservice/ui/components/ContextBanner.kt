package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class ContextBannerTone { Info, Warning, Danger }

private data class ToneColors(val bg: Color, val border: Color, val fg: Color)

private fun ContextBannerTone.colors(): ToneColors = when (this) {
    ContextBannerTone.Info -> ToneColors(
        bg = AppColors.PrimarySofter,
        border = AppColors.Primary.copy(alpha = 0.24f),
        fg = AppColors.Primary,
    )
    ContextBannerTone.Warning -> ToneColors(
        bg = AppColors.WarningSoft,
        border = AppColors.Warning.copy(alpha = 0.32f),
        fg = AppColors.Warning,
    )
    ContextBannerTone.Danger -> ToneColors(
        bg = AppColors.DangerSoft,
        border = AppColors.Danger.copy(alpha = 0.24f),
        fg = AppColors.Danger,
    )
}

@Composable
fun ContextBanner(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: String = FaIcons.CIRCLE_INFO,
    tone: ContextBannerTone = ContextBannerTone.Info,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val font = plusJakartaSansFontFamily()
    val c = tone.colors()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(c.bg)
            .border(BorderStroke(1.dp, c.border), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(20.dp).padding(top = 1.dp),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = c.fg, size = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = c.fg,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = body,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
            if (ctaLabel != null && onCta != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ctaLabel,
                    color = c.fg,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                    modifier = Modifier.clickable(onClick = onCta),
                )
            }
        }
        if (onDismiss != null) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.XMARK, color = AppColors.TextMuted, size = 12.sp)
            }
        }
    }
}
