package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

enum class ReminderUrgency(val label: String) {
    Overdue("Telat"),
    Soon("Segera"),
    Ok("Aman"),
}

fun ReminderUrgency.color(): Color = when (this) {
    ReminderUrgency.Overdue -> AppColors.Danger
    ReminderUrgency.Soon -> AppColors.Warning
    ReminderUrgency.Ok -> AppColors.Primary
}

fun ReminderUrgency.softColor(): Color = when (this) {
    ReminderUrgency.Overdue -> AppColors.DangerSoft
    ReminderUrgency.Soon -> AppColors.WarningSoft
    ReminderUrgency.Ok -> AppColors.PrimarySoft
}

@Composable
fun StatusPill(
    urgency: ReminderUrgency,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(urgency.softColor())
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        StatusDot(urgency = urgency)
        Text(
            text = urgency.label.uppercase(),
            color = urgency.color(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp,
            fontFamily = plusJakartaSansFontFamily(),
        )
    }
}

@Composable
fun StatusDot(
    urgency: ReminderUrgency,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(urgency.color()),
    )
}
