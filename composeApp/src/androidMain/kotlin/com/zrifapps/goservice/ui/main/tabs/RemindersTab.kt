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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency as DomainReminderUrgency
import com.zrifapps.goservice.feature.reminder.presentation.ReminderListViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import com.zrifapps.goservice.ui.components.AdBannerSlot
import com.zrifapps.goservice.ui.components.EmptyState
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.PullRefreshIndicator
import com.zrifapps.goservice.ui.components.PullRefreshState
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.Skeleton
import com.zrifapps.goservice.ui.components.SkeletonLeading
import com.zrifapps.goservice.ui.components.StickySearchHeader
import com.zrifapps.goservice.ui.components.color
import com.zrifapps.goservice.ui.components.softColor
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

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
    isRefreshing: Boolean = false,
    reminderVm: ReminderListViewModel = koinViewModel(),
    vehicleVm: VehicleListViewModel = koinViewModel(),
) {
    val reminderState by reminderVm.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleVm.state.collectAsStateWithLifecycle()
    val isLoading = reminderState.isLoading
    val isEmpty = reminderState.isEmpty
    val vehicleById = vehicleState.vehicles.associateBy { it.id }
    val activeCount = reminderState.reminders.size
    val selected = reminderState.urgencyFilter.toChipFilter()
    val searchQuery = reminderState.query

    if (isLoading) {
        Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            RemindersHeader(activeCount = 0, onAddReminder = onAddReminder)
            Spacer(Modifier.height(12.dp))
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
            Skeleton.Row(leading = SkeletonLeading.Icon, lines = 2)
        }
        return
    }

    if (isEmpty) {
        Column(modifier = modifier.fillMaxSize()) {
            RemindersHeader(activeCount = 0, onAddReminder = onAddReminder)
            EmptyState(
                modifier = Modifier.weight(1f),
                icon = FaIcons.BELL,
                title = "Belum ada pengingat aktif",
                body = "Buat pengingat berdasarkan KM atau tanggal supaya servis tepat waktu.",
                ctaLabel = "+ Buat Pengingat",
                onCta = onAddReminder,
            )
        }
        return
    }

    val groupedByUrgency = reminderState.reminders.groupBy { it.urgency }
    val overdue = groupedByUrgency[DomainReminderUrgency.Overdue].orEmpty()
    val soon = groupedByUrgency[DomainReminderUrgency.Soon].orEmpty()
    val ok = groupedByUrgency[DomainReminderUrgency.Ok].orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        RemindersHeader(activeCount = activeCount, onAddReminder = onAddReminder)

        StickySearchHeader(
            value = searchQuery,
            onValueChange = reminderVm::setQuery,
            placeholder = "Cari pengingat…",
        )

        if (isRefreshing) {
            PullRefreshIndicator(state = PullRefreshState.Refreshing)
        }

        FilterChips(
            selected = selected,
            onSelect = { chip -> reminderVm.setUrgencyFilter(chip.toDomainSet()) },
        )
        Spacer(Modifier.height(14.dp))
        AdBannerSlot()
        Spacer(Modifier.height(14.dp))

        ReminderGroup(
            label = "Telat — segera servis",
            accent = AppColors.Danger,
            reminders = overdue,
            vehicleById = vehicleById,
            onOpenDetail = onOpenReminderDetail,
        )
        ReminderGroup(
            label = "Akan datang",
            accent = AppColors.Warning,
            reminders = soon,
            vehicleById = vehicleById,
            onOpenDetail = onOpenReminderDetail,
        )
        ReminderGroup(
            label = "Aman",
            accent = AppColors.Primary,
            reminders = ok,
            vehicleById = vehicleById,
            onOpenDetail = onOpenReminderDetail,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ReminderGroup(
    label: String,
    accent: Color,
    reminders: List<Reminder>,
    vehicleById: Map<String, Vehicle>,
    onOpenDetail: () -> Unit,
) {
    if (reminders.isEmpty()) return
    ReminderGroupHeader(label = label, accent = accent, count = reminders.size)
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        reminders.forEach { reminder ->
            ReminderListCard(
                reminder = reminder,
                vehicle = vehicleById[reminder.vehicleId],
                onClick = onOpenDetail,
            )
        }
    }
    Spacer(Modifier.height(18.dp))
}

@Composable
private fun RemindersHeader(activeCount: Int, onAddReminder: () -> Unit) {
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
                text = "$activeCount pengingat aktif",
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
    reminder: Reminder,
    vehicle: Vehicle?,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val urgency = reminder.urgency.toUiUrgency()
    val icon = FaIcons.BELL
    val vehicleLabel = vehicle?.displayTitle ?: "—"
    val triggerLabel = reminderTriggerLabel(reminder)
    val urgencyLabel = when (reminder.urgency) {
        DomainReminderUrgency.Overdue -> "Telat"
        DomainReminderUrgency.Soon -> "Segera"
        DomainReminderUrgency.Ok -> "Aman"
    }
    val progress = when (reminder.urgency) {
        DomainReminderUrgency.Overdue -> 1f
        DomainReminderUrgency.Soon -> 0.9f
        DomainReminderUrgency.Ok -> 0.4f
    }
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
                    text = reminder.title,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = urgencyLabel,
                    color = urgency.color(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Text(
                text = "$vehicleLabel · $triggerLabel",
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

private fun DomainReminderUrgency.toUiUrgency(): ReminderUrgency = when (this) {
    DomainReminderUrgency.Overdue -> ReminderUrgency.Overdue
    DomainReminderUrgency.Soon -> ReminderUrgency.Soon
    DomainReminderUrgency.Ok -> ReminderUrgency.Ok
}

private fun reminderTriggerLabel(reminder: Reminder): String = when (val t = reminder.trigger) {
    is ReminderTrigger.ByKm -> "@ ${t.targetOdometer.kilometers} km"
    is ReminderTrigger.ByDate -> "due date set"
    is ReminderTrigger.ByBoth -> "@ ${t.targetOdometer.kilometers} km / date"
}

private fun Set<DomainReminderUrgency>.toChipFilter(): ReminderFilter = when {
    isEmpty() -> ReminderFilter.All
    size > 1 -> ReminderFilter.All
    first() == DomainReminderUrgency.Overdue -> ReminderFilter.Overdue
    first() == DomainReminderUrgency.Soon -> ReminderFilter.Soon
    first() == DomainReminderUrgency.Ok -> ReminderFilter.Ok
    else -> ReminderFilter.All
}

private fun ReminderFilter.toDomainSet(): Set<DomainReminderUrgency> = when (this) {
    ReminderFilter.All -> emptySet()
    ReminderFilter.Overdue -> setOf(DomainReminderUrgency.Overdue)
    ReminderFilter.Soon -> setOf(DomainReminderUrgency.Soon)
    ReminderFilter.Ok -> setOf(DomainReminderUrgency.Ok)
}
