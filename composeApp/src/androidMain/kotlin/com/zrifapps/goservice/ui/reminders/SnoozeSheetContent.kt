package com.zrifapps.goservice.ui.reminders

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private enum class SnoozeOption(val label: String) {
    Tomorrow("Besok pagi"),
    ThreeDays("3 hari lagi"),
    OneWeek("1 minggu lagi"),
    KmPlus100("Saat KM bertambah 100"),
    PickDate("Pilih tanggal sendiri"),
}

@Composable
fun SnoozeSheetContent(onConfirm: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    var selected by remember { mutableStateOf(SnoozeOption.ThreeDays) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 36.dp),
    ) {
        Text(
            text = "Tunda pengingat",
            color = AppColors.TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp,
            fontFamily = font,
        )
        Text(
            text = "Kapan kamu mau diingatkan lagi?",
            color = AppColors.TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp),
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SnoozeOption.entries.forEach { option ->
                SnoozeRow(
                    label = option.label,
                    active = selected == option,
                    onClick = { selected = option },
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onConfirm),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Tunda",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun SnoozeRow(label: String, active: Boolean, onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) AppColors.PrimarySoft else AppColors.SurfaceAlt)
            .border(
                BorderStroke(1.5.dp, if (active) AppColors.Primary else Color.Transparent),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(PaddingValues(14.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        if (active) {
            FaIcon(icon = FaIcons.CHECK, color = AppColors.Primary, size = 16.sp)
        }
    }
}
