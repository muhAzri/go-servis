package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun ErrorState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: String = FaIcons.CIRCLE_INFO,
    retryLabel: String? = "Coba lagi",
    onRetry: (() -> Unit)? = null,
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
                .background(AppColors.DangerSoft),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = icon, color = AppColors.Danger, size = 40.sp)
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
        if (retryLabel != null && onRetry != null) {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(AppColors.Primary)
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                FaIcon(icon = FaIcons.ARROW_ROTATE_RIGHT, color = Color.White, size = 13.sp)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = retryLabel,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
