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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency as DomainReminderUrgency
import com.zrifapps.goservice.feature.reminder.presentation.ReminderListViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.NativeAdCard
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.components.StatusPill
import com.zrifapps.goservice.ui.components.color
import com.zrifapps.goservice.ui.components.softColor
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    userName: String = "",
    onOpenReminders: () -> Unit = {},
    onOpenReminderDetail: (String) -> Unit = {},
    onOpenVehicleDetail: () -> Unit = {},
    onOpenVehicleList: () -> Unit = {},
    onAddService: () -> Unit = {},
    onAddVehicle: () -> Unit = {},
    onUpdateOdometer: () -> Unit = {},
    vehicleVm: VehicleListViewModel = koinViewModel(),
    reminderVm: ReminderListViewModel = koinViewModel(),
) {
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()
    val reminderState by reminderVm.state.collectAsStateWithLifecycle()
    val isLoading = vehicleState.isLoading || reminderState.isLoading
    val isEmpty = vehicleState.isEmpty

    if (isLoading) {
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            HomeHeader(userName = userName, onOpenReminders = onOpenReminders)
            Spacer(Modifier.height(8.dp))
            Skeleton.Card(height = 150.dp, lines = 3)
            Spacer(Modifier.height(12.dp))
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
        }
        return
    }

    if (isEmpty) {
        Column(modifier = modifier.fillMaxSize()) {
            HomeHeader(userName = userName, onOpenReminders = onOpenReminders)
            EmptyState(
                modifier = Modifier.weight(1f),
                icon = FaIcons.MOTORCYCLE,
                title = "Belum ada kendaraan",
                body = "Tambah motor atau mobilmu untuk mulai catat servis & dapat pengingat.",
                ctaLabel = "+ Tambah Kendaraan",
                onCta = onAddVehicle,
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HomeHeader(userName = userName, onOpenReminders = onOpenReminders)

        val vehicleById = vehicleState.vehicles.associateBy { it.id }
        val heroVehicle = pickHeroVehicle(vehicleState.vehicles, reminderState.reminders, vehicleById)
        if (heroVehicle != null) {
            HeroStatusCard(
                vehicle = heroVehicle,
                overdueCount = reminderState.reminders.count {
                    it.vehicleId == heroVehicle.id && it.urgency == DomainReminderUrgency.Overdue
                },
                onOpenVehicleDetail = onOpenVehicleDetail,
            )
            Spacer(Modifier.height(16.dp))
        }
        AdBannerSlot()
        Spacer(Modifier.height(8.dp))
        QuickActionsRow(
            onAddService = onAddService,
            onUpdateOdometer = onUpdateOdometer,
            onAddVehicle = onAddVehicle,
        )
        Spacer(Modifier.height(16.dp))
        SectionHeading(title = "Pengingat aktif", actionLabel = "Lihat semua", onAction = onOpenReminders)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            val topReminders = reminderState.reminders.take(3)
            if (topReminders.isEmpty()) {
                ReminderEmptyHint()
            } else {
                topReminders.forEach { reminder ->
                    ReminderRow(
                        title = reminder.title,
                        subtitle = reminderSubtitle(reminder, vehicleById[reminder.vehicleId]),
                        urgency = reminder.urgency.toUi(),
                        onClick = { onOpenReminderDetail(reminder.id) },
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        NativeAdCard()
        Spacer(Modifier.height(12.dp))
        SectionHeading(title = "Kendaraan saya", actionLabel = "Lihat semua", onAction = onOpenVehicleList)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            vehicleState.vehicles.take(4).forEach { vehicle ->
                VehicleSummaryRow(
                    vehicle = vehicle,
                    onClick = onOpenVehicleDetail,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }

}

@Composable
private fun HomeHeader(userName: String, onOpenReminders: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    val greetingName = userName.ifBlank { "Kamu" }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Halo, $greetingName 👋",
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
            Text(
                text = "Garasi Saya",
                color = AppColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
                fontFamily = font,
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenReminders),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(icon = FaIcons.BELL, color = AppColors.TextPrimary, size = 18.sp)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AppColors.Danger)
                    .border(2.dp, AppColors.Surface, CircleShape),
            )
        }
    }
}

@Composable
private fun HeroStatusCard(
    vehicle: Vehicle,
    overdueCount: Int,
    onOpenVehicleDetail: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val tone = if (overdueCount > 0) AppColors.Danger else AppColors.Primary
    val headline = when {
        overdueCount == 1 -> "1 servis telat — segera bawa ke bengkel"
        overdueCount > 1 -> "$overdueCount servis telat — segera bawa ke bengkel"
        else -> "Servis terpantau — semua aman"
    }
    val icon = if (vehicle.type == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(tone)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon(icon = icon, color = Color.White.copy(alpha = 0.95f), size = 14.sp)
            Spacer(Modifier.size(8.dp))
            Text(
                text = vehicle.displayTitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = headline,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            lineHeight = 28.sp,
            fontFamily = font,
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.25f)),
        )
        Spacer(Modifier.height(14.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeroStat(label = "KM saat ini", value = formatKm(vehicle.odometer.kilometers), monoSize = 18.sp)
            HeroStat(
                label = "Plat",
                value = vehicle.plateNumber.ifBlank { "—" },
                monoSize = 16.sp,
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f))
                    .clickable(onClick = onOpenVehicleDetail)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Detail →",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

private fun pickHeroVehicle(
    vehicles: List<Vehicle>,
    reminders: List<Reminder>,
    vehicleById: Map<String, Vehicle>,
): Vehicle? {
    val overdueReminder = reminders.firstOrNull { it.urgency == DomainReminderUrgency.Overdue }
    overdueReminder?.let { vehicleById[it.vehicleId] }?.also { return it }
    return vehicles.firstOrNull()
}

private fun reminderSubtitle(reminder: Reminder, vehicle: Vehicle?): String {
    val nick = vehicle?.displayTitle ?: "—"
    val urgencyLabel = when (reminder.urgency) {
        DomainReminderUrgency.Overdue -> "Telat"
        DomainReminderUrgency.Soon -> "Segera"
        DomainReminderUrgency.Ok -> "Aman"
    }
    return "$nick · $urgencyLabel"
}

private fun DomainReminderUrgency.toUi(): ReminderUrgency = when (this) {
    DomainReminderUrgency.Overdue -> ReminderUrgency.Overdue
    DomainReminderUrgency.Soon -> ReminderUrgency.Soon
    DomainReminderUrgency.Ok -> ReminderUrgency.Ok
}

private fun formatKm(km: Long): String {
    val parts = mutableListOf<String>()
    var n = km
    if (n == 0L) return "0"
    while (n > 0) {
        val chunk = n % 1000
        n /= 1000
        if (n > 0) parts.add(0, chunk.toString().padStart(3, '0'))
        else parts.add(0, chunk.toString())
    }
    return parts.joinToString(".")
}

@Composable
private fun ReminderEmptyHint() {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text(
            text = "Belum ada pengingat aktif. Tambah dari halaman Pengingat.",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontFamily = font,
        )
    }
}

@Composable
private fun HeroStat(label: String, value: String, monoSize: TextUnit) {
    val font = plusJakartaSansFontFamily()
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontFamily = font,
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = monoSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun QuickActionsRow(
    onAddService: () -> Unit,
    onUpdateOdometer: () -> Unit,
    onAddVehicle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionTile(
            icon = FaIcons.PLUS,
            label = "Catat Servis",
            primary = true,
            onClick = onAddService,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            icon = FaIcons.GAUGE,
            label = "Update KM",
            primary = false,
            onClick = onUpdateOdometer,
            modifier = Modifier.weight(1f),
        )
        QuickActionTile(
            icon = FaIcons.CAR,
            label = "Tambah",
            primary = false,
            onClick = onAddVehicle,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionTile(
    icon: String,
    label: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (primary) AppColors.Primary else AppColors.Surface)
            .then(
                if (primary) Modifier
                else Modifier.border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(16.dp))
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FaIcon(
            icon = icon,
            color = if (primary) Color.White else AppColors.TextPrimary,
            size = 20.sp,
        )
        Text(
            text = label,
            color = if (primary) Color.White else AppColors.TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun SectionHeading(title: String, actionLabel: String?, onAction: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = AppColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                color = AppColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

@Composable
private fun ReminderRow(
    title: String,
    subtitle: String,
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
            icon = FaIcons.BELL,
            foreground = urgency.color(),
            background = urgency.softColor(),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = subtitle,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = font,
            )
        }
        StatusPill(urgency = urgency)
    }
}

@Composable
private fun VehicleSummaryRow(
    vehicle: Vehicle,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val accent = parseHexColor(vehicle.color.value) ?: AppColors.Primary
    val icon = if (vehicle.type == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE
    val plate = vehicle.plateNumber.ifBlank { "—" }
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
            foreground = accent,
            background = accent.copy(alpha = 0.13f),
            size = 48.dp,
            iconSize = 24.sp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = vehicle.displayTitle,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = "$plate · ${formatKm(vehicle.odometer.kilometers)} km",
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 14.sp)
    }
}

internal fun parseHexColor(hex: String): Color? = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Throwable) {
    null
}
