package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun OfflineBanner(
    modifier: Modifier = Modifier,
    title: String = "Tidak ada koneksi internet",
    body: String = "Beberapa data mungkin tertunda. Akan disinkronkan otomatis.",
    retryLabel: String? = null,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.WarningSoft)
            .border(1.dp, AppColors.Warning.copy(alpha = 0.32f), RoundedCornerShape(0.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(20.dp).padding(top = 1.dp),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.CIRCLE_INFO, color = AppColors.Warning, size = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.Warning,
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
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (retryLabel != null && onRetry != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(AppColors.Surface)
                    .border(1.dp, AppColors.Warning.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
                    .clickable(onClick = onRetry)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    FaIcon(icon = FaIcons.ARROW_ROTATE_RIGHT, color = AppColors.Warning, size = 11.sp)
                    Text(
                        text = retryLabel,
                        color = AppColors.Warning,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }
        }
        if (onDismiss != null) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                FaIcon(icon = FaIcons.XMARK, color = AppColors.TextMuted, size = 11.sp)
            }
        }
    }
}
