package com.zrifapps.goservice.ui.main.tabs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.color
import com.zrifapps.goservice.ui.components.softColor
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class ReminderFilter(val label: String) {
    All("Semua"),
    Overdue("Telat"),
    Soon("Soon"),
    Ok("Aman"),
}

@Composable
fun RemindersTab(
    modifier: Modifier = Modifier,
    onOpenReminderDetail: () -> Unit = {},
    onAddReminder: () -> Unit = {},
) {
    var selected by remember { mutableStateOf(ReminderFilter.All) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        RemindersHeader(onAddReminder = onAddReminder)

        FilterChips(
            selected = selected,
            onSelect = { selected = it },
        )
        Spacer(Modifier.height(14.dp))
        AdBannerSlot()
        Spacer(Modifier.height(14.dp))

        ReminderGroupHeader(label = "Telat — segera servis", accent = AppColors.Danger, count = 1)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ReminderListCard(
                icon = FaIcons.OIL_CAN,
                title = "Ganti Oli Mesin",
                vehicle = "Beat Hitam",
                dueDate = "20 Apr 2026",
                daysLeftLabel = "+16h",
                progress = 1.0f,
                urgency = ReminderUrgency.Overdue,
                onClick = onOpenReminderDetail,
            )
        }
        Spacer(Modifier.height(18.dp))

        ReminderGroupHeader(label = "Akan datang", accent = AppColors.Warning, count = 3)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ReminderListCard(
                icon = FaIcons.CIRCLE_NOTCH,
                title = "Kampas Rem",
                vehicle = "Beat Hitam",
                dueDate = "25 Mei 2026",
                daysLeftLabel = "19h",
                progress = 0.97f,
                urgency = ReminderUrgency.Soon,
                onClick = onOpenReminderDetail,
            )
            ReminderListCard(
                icon = FaIcons.OIL_CAN,
                title = "Ganti Oli Mesin",
                vehicle = "Vario Merah",
                dueDate = "12 Mei 2026",
                daysLeftLabel = "6h",
                progress = 0.9f,
                urgency = ReminderUrgency.Soon,
                onClick = onOpenReminderDetail,
            )
            ReminderListCard(
                icon = FaIcons.WRENCH,
                title = "Servis Berkala",
                vehicle = "Brio Biru",
                dueDate = "9 Mei 2026",
                daysLeftLabel = "3h",
                progress = 0.98f,
                urgency = ReminderUrgency.Soon,
                onClick = onOpenReminderDetail,
            )
        }
        Spacer(Modifier.height(18.dp))

        ReminderGroupHeader(label = "Aman", accent = AppColors.Primary, count = 2)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ReminderListCard(
                icon = FaIcons.FILTER,
                title = "Filter Oli & Udara",
                vehicle = "Avanza Putih",
                dueDate = "10 Jul 2026",
                daysLeftLabel = "65h",
                progress = 0.95f,
                urgency = ReminderUrgency.Ok,
                onClick = onOpenReminderDetail,
            )
            ReminderListCard(
                icon = FaIcons.CAR_BATTERY,
                title = "Aki",
                vehicle = "Avanza Putih",
                dueDate = "1 Sep 2026",
                daysLeftLabel = "118h",
                progress = 0.4f,
                urgency = ReminderUrgency.Ok,
                onClick = onOpenReminderDetail,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun RemindersHeader(onAddReminder: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                PaddingValues(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "6 pengingat aktif",
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
            Text(
                text = "Pengingat Servis",
                color = AppColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                fontFamily = font,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        com.zrifapps.goservice.ui.components.CircleIconButton(
            icon = FaIcons.PLUS,
            onClick = onAddReminder,
            iconColor = AppColors.Primary,
        )
    }
}

@Composable
private fun FilterChips(selected: ReminderFilter, onSelect: (ReminderFilter) -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReminderFilter.entries.forEach { filter ->
            val active = filter == selected
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) AppColors.Primary else AppColors.Surface)
                    .then(
                        if (active) Modifier
                        else Modifier.border(BorderStroke(1.dp, AppColors.Border), CircleShape)
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = filter.label,
                    color = if (active) Color.White else AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun ReminderGroupHeader(label: String, accent: Color, count: Int) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Text(
            text = "${label.uppercase()} ($count)",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = font,
        )
    }
}

@Composable
private fun ReminderListCard(
    icon: String,
    title: String,
    vehicle: String,
    dueDate: String,
    daysLeftLabel: String,
    progress: Float,
    urgency: ReminderUrgency,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = icon,
            foreground = urgency.color(),
            background = urgency.softColor(),
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = daysLeftLabel,
                    color = urgency.color(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Text(
                text = "$vehicle · $dueDate",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = font,
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppColors.SurfaceAlt),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .background(urgency.color()),
                )
            }
        }
    }
}
