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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.usecase.SnoozeReminder
import com.zrifapps.goservice.feature.reminder.presentation.ReminderDetailViewModel
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.IconBadge
import com.zrifapps.goservice.ui.components.ReminderUrgency as UiReminderUrgency
import com.zrifapps.goservice.ui.components.StatusPill
import com.zrifapps.goservice.ui.service.components.formatKmDisplay
import com.zrifapps.goservice.ui.service.components.formatServiceDate
import com.zrifapps.goservice.ui.service.components.serviceTypeMeta
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    reminderId: String,
    onBack: () -> Unit,
    onMarkServiced: (reminderId: String, vehicleId: String) -> Unit = { _, _ -> },
    onEdit: (reminderId: String) -> Unit = {},
    onDeleted: () -> Unit = {},
    vm: ReminderDetailViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    LaunchedEffect(reminderId) { vm.load(reminderId) }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                ReminderDetailViewModel.Event.Deleted,
                ReminderDetailViewModel.Event.Dismissed -> onDeleted()
                ReminderDetailViewModel.Event.Completed,
                ReminderDetailViewModel.Event.Snoozed -> Unit
                is ReminderDetailViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
    }

    var showSnoozeSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snoozeState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val reminder = state.reminder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(
            onBack = onBack,
            onEdit = { onEdit(reminderId) },
            onDelete = { showDeleteDialog = true },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            if (reminder == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (state.isLoading) "Memuat…" else "Pengingat tidak ditemukan",
                        color = AppColors.TextMuted,
                        fontFamily = plusJakartaSansFontFamily(),
                        fontSize = 14.sp,
                    )
                }
                return@Column
            }

            val meta = serviceTypeMeta(reminder.serviceType.key)
            val vehicle = state.vehicle
            val vehicleLine = vehicle?.let {
                val plate = it.plateNumber.takeIf(String::isNotBlank)
                if (plate != null) "${it.displayTitle} · $plate" else it.displayTitle
            } ?: "Kendaraan dihapus"

            HeaderRow(
                icon = meta.icon,
                accent = meta.color,
                title = reminder.title,
                subtitle = vehicleLine,
                urgency = reminder.urgency.toUi(),
            )
            Spacer(Modifier.height(16.dp))
            FactsCard(trigger = reminder.trigger, notifyDays = reminder.notifyDaysBefore)
            Spacer(Modifier.height(20.dp))

            val note = reminder.note?.takeIf(String::isNotBlank)
            if (note != null) {
                SectionLabel("Catatan")
                NoteBlock(text = note)
                Spacer(Modifier.height(20.dp))
            }

            if (reminder.status == ReminderStatus.Active || reminder.status == ReminderStatus.Snoozed) {
                ActionRow(
                    onMarkServiced = { onMarkServiced(reminder.id, reminder.vehicleId) },
                    onSnooze = { showSnoozeSheet = true },
                    busy = state.isBusy,
                )
                Spacer(Modifier.height(24.dp))
            } else {
                StatusBlock(status = reminder.status)
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showSnoozeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSnoozeSheet = false },
            sheetState = snoozeState,
            containerColor = AppColors.Surface,
        ) {
            SnoozeSheetContent(
                onPick = { duration ->
                    vm.snooze(duration)
                    showSnoozeSheet = false
                },
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus pengingat?") },
            text = { Text("Pengingat ini akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.delete()
                }) { Text("Hapus", color = AppColors.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
            containerColor = AppColors.Surface,
        )
    }
}

private fun com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency.toUi(): UiReminderUrgency =
    when (this) {
        com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency.Ok -> UiReminderUrgency.Ok
        com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency.Soon -> UiReminderUrgency.Soon
        com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency.Overdue -> UiReminderUrgency.Overdue
    }

@Composable
private fun TopBar(onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Text(
            text = "Detail Pengingat",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        CircleIconButton(icon = FaIcons.PEN_TO_SQUARE, onClick = onEdit)
        CircleIconButton(icon = FaIcons.TRASH, onClick = onDelete)
    }
}

@Composable
private fun HeaderRow(
    icon: String,
    accent: Color,
    title: String,
    subtitle: String,
    urgency: UiReminderUrgency,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconBadge(
            icon = icon,
            foreground = accent,
            background = accent.copy(alpha = 0.15f),
            size = 56.dp,
            iconSize = 28.sp,
            corner = 16.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            Text(
                text = subtitle,
                color = AppColors.TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        StatusPill(urgency = urgency)
    }
}

@Composable
private fun FactsCard(trigger: ReminderTrigger, notifyDays: Int) {
    val font = plusJakartaSansFontFamily()
    val rows = buildList {
        when (trigger) {
            is ReminderTrigger.ByKm -> add(Triple(FaIcons.GAUGE, formatKmDisplay(trigger.targetOdometer.kilometers), true))
            is ReminderTrigger.ByDate -> add(Triple(FaIcons.CALENDAR, formatServiceDate(trigger.targetDate), false))
            is ReminderTrigger.ByBoth -> {
                add(Triple(FaIcons.GAUGE, formatKmDisplay(trigger.targetOdometer.kilometers), true))
                add(Triple(FaIcons.CALENDAR, formatServiceDate(trigger.targetDate), false))
            }
        }
        add(Triple(FaIcons.BELL, "Notif $notifyDays hari sebelumnya", false))
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp)),
    ) {
        rows.forEachIndexed { index, (icon, value, mono) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FaIcon(icon = icon, color = AppColors.TextMuted, size = 16.sp)
                Text(
                    text = value,
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = if (mono) FontFamily.Monospace else font,
                )
            }
            if (index < rows.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = font,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
    )
}

@Composable
private fun NoteBlock(text: String) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = text,
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun ActionRow(onMarkServiced: () -> Unit, onSnooze: () -> Unit, busy: Boolean) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (busy) AppColors.Primary.copy(alpha = 0.4f) else AppColors.Primary)
                .clickable(enabled = !busy, onClick = onMarkServiced),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 14.sp)
                Text(
                    text = "Tandai sudah servis",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(enabled = !busy, onClick = onSnooze),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FaIcon(icon = FaIcons.CLOCK, color = AppColors.TextPrimary, size = 13.sp)
                Text(
                    text = "Tunda pengingat",
                    color = AppColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun StatusBlock(status: ReminderStatus) {
    val font = plusJakartaSansFontFamily()
    val label = when (status) {
        ReminderStatus.Completed -> "Sudah selesai"
        ReminderStatus.Dismissed -> "Dilewatkan"
        else -> "—"
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.PrimarySoft)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = AppColors.Primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

@Composable
private fun SnoozeSheetContent(onPick: (SnoozeReminder.SnoozeDuration) -> Unit) {
    val font = plusJakartaSansFontFamily()
    val options = listOf(
        SnoozeReminder.SnoozeDuration.OneDay to "Besok",
        SnoozeReminder.SnoozeDuration.ThreeDays to "3 hari lagi",
        SnoozeReminder.SnoozeDuration.OneWeek to "1 minggu lagi",
        SnoozeReminder.SnoozeDuration.TwoWeeks to "2 minggu lagi",
        SnoozeReminder.SnoozeDuration.OneMonth to "1 bulan lagi",
    )
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
            options.forEach { (duration, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.SurfaceAlt)
                        .clickable { onPick(duration) }
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
                    FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 12.sp)
                }
            }
        }
    }
}
