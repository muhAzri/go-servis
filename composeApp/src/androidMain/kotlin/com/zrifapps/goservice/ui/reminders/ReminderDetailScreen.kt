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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.NativeAdCard
import com.zrifapps.goservice.ui.components.ReminderUrgency
import com.zrifapps.goservice.ui.components.StatusPill
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    onBack: () -> Unit,
    onMarkServiced: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var showSnooze by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snoozeState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        ReminderDetailTopBar(
            onBack = onBack,
            onEdit = onEdit,
            onDelete = { showDeleteDialog = true },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            HeaderBlock()
            Spacer(Modifier.height(16.dp))
            OverdueStatsCard()
            Spacer(Modifier.height(16.dp))
            DetailSectionLabel("Detail servis")
            ServiceDetailRows()
            Spacer(Modifier.height(8.dp))
            NativeAdCard()
            Spacer(Modifier.height(24.dp))
        }

        ReminderActionBar(
            onSnooze = { showSnooze = true },
            onMarkServiced = onMarkServiced,
        )
    }

    if (showSnooze) {
        ModalBottomSheet(
            onDismissRequest = { showSnooze = false },
            sheetState = snoozeState,
            containerColor = AppColors.Surface,
        ) {
            SnoozeSheetContent(onConfirm = {
                scope.launch { snoozeState.hide() }.invokeOnCompletion {
                    showSnooze = false
                }
            })
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Hapus pengingat?") },
            text = {
                Text(text = "Pengingat ini akan dihapus dan tidak akan muncul lagi di lock screen.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text(text = "Hapus", color = AppColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
            containerColor = AppColors.Surface,
        )
    }
}

@Composable
private fun ReminderDetailTopBar(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(icon = FaIcons.CHEVRON_LEFT, onClick = onBack)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircleIconButton(icon = FaIcons.PEN_TO_SQUARE, onClick = onEdit)
            CircleIconButton(
                icon = FaIcons.TRASH,
                onClick = onDelete,
                iconColor = AppColors.Danger,
            )
        }
    }
}

@Composable
private fun HeaderBlock() {
    val font = plusJakartaSansFontFamily()
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        StatusPill(urgency = ReminderUrgency.Overdue)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Ganti Oli Mesin",
            color = AppColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            fontFamily = font,
        )
        Text(
            text = "Beat Hitam · Honda BeAT 110 2022",
            color = AppColors.TextMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
    }
}

@Composable
private fun OverdueStatsCard() {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppColors.DangerSoft)
            .border(BorderStroke(1.dp, AppColors.Danger.copy(alpha = 0.19f)), RoundedCornerShape(22.dp))
            .padding(20.dp),
    ) {
        Text(
            text = "TELAT 16 HARI · 420 KM",
            color = AppColors.Danger,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp,
            fontFamily = font,
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            StatBlock(label = "Target servis", value = "18.000 km", trailing = "20 Apr 2026", valueColor = AppColors.TextPrimary)
            StatBlock(label = "KM sekarang", value = "18.420 km", trailing = "diperbarui 2h lalu", valueColor = AppColors.Danger)
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.06f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(AppColors.Danger),
            )
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.StatBlock(
    label: String,
    value: String,
    trailing: String,
    valueColor: Color,
) {
    val font = plusJakartaSansFontFamily()
    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
        Text(
            text = trailing,
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        )
    }
}

@Composable
private fun DetailSectionLabel(text: String, trailing: String? = null) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text.uppercase(),
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = font,
        )
        if (trailing != null) {
            Text(
                text = trailing,
                color = AppColors.Warning,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun ServiceDetailRows() {
    val rows = listOf(
        "Interval" to "2.000 km / 2 bln",
        "Servis terakhir" to "20 Feb 2026 · 16.000 km",
        "Bengkel terakhir" to "AHASS Kebon Jeruk",
        "Biaya terakhir" to "Rp 65.000",
    )
    val font = plusJakartaSansFontFamily()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp),
    ) {
        rows.forEachIndexed { index, (k, v) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = k,
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                )
                Text(
                    text = v,
                    color = AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
            }
            if (index < rows.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(AppColors.Border),
                )
            }
        }
    }
}

@Composable
private fun ReminderActionBar(onSnooze: () -> Unit, onMarkServiced: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.SurfaceAlt)
                .clickable(onClick = onSnooze),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Tunda",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Primary)
                .clickable(onClick = onMarkServiced),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 14.sp)
                Text(
                    text = "Tandai Sudah Servis",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}
