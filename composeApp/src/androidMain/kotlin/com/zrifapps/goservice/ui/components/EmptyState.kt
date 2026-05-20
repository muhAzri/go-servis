package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun EmptyState(
    icon: String,
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    ctaLabel: String? = null,
    onCta: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    iconForeground: Color = AppColors.Primary,
    iconBackground: Color = AppColors.PrimarySoft,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(PaddingValues(horizontal = 24.dp, vertical = 32.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = iconForeground, size = 40.sp)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            color = AppColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = body,
            color = AppColors.TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.widthIn(max = 280.dp),
        )

        if (ctaLabel != null && onCta != null) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .widthIn(min = 220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AppColors.Primary)
                    .clickable(onClick = onCta)
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ctaLabel,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }

        if (secondaryLabel != null && onSecondary != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = secondaryLabel,
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = onSecondary),
            )
        }
    }
}
