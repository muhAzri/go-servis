package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun DangerZoneCard(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sectionTitle: String = "ZONA BAHAYA",
    description: String? = null,
    icon: String = FaIcons.TRASH,
) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = sectionTitle.uppercase(),
            color = AppColors.Danger,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            fontFamily = font,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.DangerSoft.copy(alpha = 0.45f))
                .border(1.5.dp, AppColors.Danger.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = icon, color = AppColors.Danger, size = 15.sp)
            Text(
                text = label,
                color = AppColors.Danger,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                lineHeight = 17.sp,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}
