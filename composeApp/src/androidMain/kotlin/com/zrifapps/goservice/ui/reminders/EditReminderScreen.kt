package com.zrifapps.goservice.ui.reminders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.ConfirmDialog
import com.zrifapps.goservice.ui.components.ConfirmDialogTone
import com.zrifapps.goservice.ui.components.DangerZoneCard
import com.zrifapps.goservice.ui.components.DEFAULT_VEHICLE_OPTIONS
import com.zrifapps.goservice.ui.components.FormShell
import com.zrifapps.goservice.ui.components.VehiclePickerRow
import com.zrifapps.goservice.ui.components.VehiclePickerSheet
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

enum class ReminderStatus { Active, Inactive }

@Composable
fun EditReminderScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val vehicles = remember { DEFAULT_VEHICLE_OPTIONS }
    var selectedVehicle by remember { mutableStateOf(vehicles.first()) }
    var showVehicleSheet by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(ReminderStatus.Active) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    FormShell(
        title = "Edit Pengingat",
        onClose = onBack,
        onSave = onSave,
        saveEnabled = true,
        saveLabel = "Simpan",
        footer = {
            AppButton(text = "Simpan Perubahan", onClick = onSave)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Section("Kendaraan") {
                VehiclePickerRow(
                    selected = selectedVehicle,
                    onClick = { showVehicleSheet = true },
                )
            }

            Section("Status pengingat") {
                StatusSegmented(selected = status, onSelect = { status = it })
            }

            Section("Catatan") {
                Text(
                    text = "Sesuaikan target KM atau tanggal sesuai kebutuhan. Pengingat akan dimulai 7 hari sebelum jadwal.",
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = plusJakartaSansFontFamily(),
                    lineHeight = 18.sp,
                )
            }

            Spacer(Modifier.height(4.dp))
            DangerZoneCard(
                label = "Hapus pengingat ini",
                description = "Pengingat ini akan dihapus dan tidak akan muncul lagi.",
                onClick = { showDeleteDialog = true },
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showVehicleSheet) {
        VehiclePickerSheet(
            options = vehicles,
            selectedId = selectedVehicle.id,
            onDismiss = { showVehicleSheet = false },
            onPick = { selectedVehicle = it },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Hapus pengingat ini?",
            body = "Pengingat ini akan dihapus permanen.",
            icon = FaIcons.TRASH,
            tone = ConfirmDialogTone.Danger,
            confirmLabel = "Hapus",
            cancelLabel = "Batal",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
        )
    }
}

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Column {
        Text(
            text = label.uppercase(),
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun StatusSegmented(selected: ReminderStatus, onSelect: (ReminderStatus) -> Unit) {
    val font = plusJakartaSansFontFamily()
    val options = listOf(
        ReminderStatus.Active to "Aktif",
        ReminderStatus.Inactive to "Nonaktif",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceAlt)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { (mode, label) ->
            val active = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (active) AppColors.Surface else Color.Transparent)
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) AppColors.TextPrimary else AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditReminderScreenPreview() {
    EditReminderScreen()
}
