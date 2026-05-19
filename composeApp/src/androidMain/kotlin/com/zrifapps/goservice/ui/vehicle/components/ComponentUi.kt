package com.zrifapps.goservice.ui.vehicle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun TagBadge(tag: ComponentTag, modifier: Modifier = Modifier) {
    val font = plusJakartaSansFontFamily()
    val (fg, bg) = when (tag) {
        ComponentTag.Core -> AppColors.Primary to AppColors.PrimarySoft
        ComponentTag.Plus -> AppColors.Warning to AppColors.WarningSoft
        ComponentTag.Pro  -> AppColors.TextMuted to AppColors.SurfaceAlt
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = tag.label().uppercase(),
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            fontFamily = font,
        )
    }
}

@Composable
fun SubtypeBadge(label: String, modifier: Modifier = Modifier) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(AppColors.PrimarySoft)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = AppColors.Primary,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp,
            fontFamily = font,
        )
    }
}

@Composable
fun ToggleSwitch(on: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (on) AppColors.Primary else Color(0x33141E0F))
            .padding(3.dp),
        contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}
