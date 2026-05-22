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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.NativeAdCard
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.components.StatusPill
import com.zrifapps.goservice.ui.onboarding.NotifPermissionSheet
import com.zrifapps.goservice.ui.components.color
import com.zrifapps.goservice.ui.components.softColor
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun HomeTab(
    modifier: Modifier = Modifier,
    userName: String = "",
    onOpenReminders: () -> Unit = {},
    onOpenReminderDetail: () -> Unit = {},
    onOpenVehicleDetail: () -> Unit = {},
    onOpenVehicleList: () -> Unit = {},
    onAddService: () -> Unit = {},
    onAddVehicle: () -> Unit = {},
    onUpdateOdometer: () -> Unit = {},
    onOpenTips: () -> Unit = {},
    notifPermissionGranted: Boolean = false,
    isEmpty: Boolean = false,
    isLoading: Boolean = false,
) {
    var bannerDismissed by remember { mutableStateOf(false) }
    var showNotifSheet by remember { mutableStateOf(false) }
    val showBanner = !notifPermissionGranted && !bannerDismissed && !isEmpty && !isLoading

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
                secondaryLabel = "Pelajari dulu",
                onSecondary = onOpenTips,
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

        if (showBanner) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                ContextBanner(
                    title = "Notif belum aktif",
                    body = "Pengingat servis tidak akan muncul di lock screen. Aktifkan supaya tidak kelewat.",
                    tone = ContextBannerTone.Warning,
                    ctaLabel = "Aktifkan →",
                    onCta = { showNotifSheet = true },
                    onDismiss = { bannerDismissed = true },
                )
            }
        }
        HeroStatusCard(onOpenVehicleDetail = onOpenVehicleDetail)
        Spacer(Modifier.height(16.dp))
        AdBannerSlot()
        Spacer(Modifier.height(8.dp))
        QuickActionsRow(
            onAddService = onAddService,
            onUpdateOdometer = onUpdateOdometer,
            onAddVehicle = onAddVehicle,
            onOpenTips = onOpenTips,
        )
        Spacer(Modifier.height(16.dp))
        SectionHeading(title = "Pengingat aktif", actionLabel = "Lihat semua", onAction = onOpenReminders)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ReminderRow(
                icon = FaIcons.OIL_CAN,
                title = "Ganti Oli Mesin",
                subtitle = "Beat Hitam · Telat 16 hari",
                urgency = ReminderUrgency.Overdue,
                onClick = onOpenReminderDetail,
            )
            ReminderRow(
                icon = FaIcons.CIRCLE_NOTCH,
                title = "Kampas Rem",
                subtitle = "Beat Hitam · 19 hari lagi",
                urgency = ReminderUrgency.Soon,
                onClick = onOpenReminderDetail,
            )
            ReminderRow(
                icon = FaIcons.FILTER,
                title = "Filter Oli & Udara",
                subtitle = "Avanza Putih · 65 hari lagi",
                urgency = ReminderUrgency.Ok,
                onClick = onOpenReminderDetail,
            )
        }
        Spacer(Modifier.height(12.dp))
        NativeAdCard()
        Spacer(Modifier.height(12.dp))
        SectionHeading(title = "Kendaraan saya", actionLabel = "Lihat semua", onAction = onOpenVehicleList)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            VehicleSummaryRow(
                icon = FaIcons.MOTORCYCLE,
                accent = Color(0xFF2E8B57),
                title = "Beat Hitam",
                plateAndKm = "B 4521 KZA · 18.420 km",
                onClick = onOpenVehicleDetail,
            )
            VehicleSummaryRow(
                icon = FaIcons.MOTORCYCLE,
                accent = Color(0xFFD6453A),
                title = "Vario Merah",
                plateAndKm = "B 6789 SKR · 8.100 km",
                onClick = onOpenVehicleDetail,
            )
            VehicleSummaryRow(
                icon = FaIcons.CAR,
                accent = Color(0xFF3F4D5C),
                title = "Avanza Putih",
                plateAndKm = "B 1234 ABC · 62.300 km",
                onClick = onOpenVehicleDetail,
            )
            VehicleSummaryRow(
                icon = FaIcons.CAR,
                accent = Color(0xFF3FB1D6),
                title = "Brio Biru",
                plateAndKm = "B 9876 XYZ · 24.500 km",
                onClick = onOpenVehicleDetail,
            )
        }
        Spacer(Modifier.height(20.dp))
    }

    if (showNotifSheet) {
        NotifPermissionSheet(
            onDismiss = { showNotifSheet = false },
            onOpenSystemSettings = { showNotifSheet = false },
        )
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
private fun HeroStatusCard(onOpenVehicleDetail: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AppColors.Danger)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaIcon(icon = FaIcons.CAR, color = Color.White.copy(alpha = 0.95f), size = 14.sp)
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Toyota Avanza Veloz",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "1 servis telat — segera bawa ke bengkel",
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
            HeroStat(label = "KM saat ini", value = "62.300", monoSize = 18.sp)
            HeroStat(label = "Plat", value = "B 1234 ABC", monoSize = 16.sp)
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
    onOpenTips: () -> Unit,
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
        QuickActionTile(
            icon = FaIcons.LIGHTBULB,
            label = "Tips",
            primary = false,
            onClick = onOpenTips,
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
    icon: String,
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
            icon = icon,
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
    icon: String,
    accent: Color,
    title: String,
    plateAndKm: String,
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
            foreground = accent,
            background = accent.copy(alpha = 0.13f),
            size = 48.dp,
            iconSize = 24.sp,
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
                text = plateAndKm,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
        FaIcon(icon = FaIcons.CHEVRON_RIGHT, color = AppColors.TextSubtle, size = 14.sp)
    }
}
