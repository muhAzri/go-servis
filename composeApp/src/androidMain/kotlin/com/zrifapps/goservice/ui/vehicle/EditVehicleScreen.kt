package com.zrifapps.goservice.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.ConfirmDialog
import com.zrifapps.goservice.ui.components.ConfirmDialogTone
import com.zrifapps.goservice.ui.components.DangerZoneCard
import com.zrifapps.goservice.ui.components.FormShell
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.vehicle.components.VehicleForm
import com.zrifapps.goservice.ui.vehicle.components.VehicleFormState

@Composable
fun EditVehicleScreen(
    modifier: Modifier = Modifier,
    initial: VehicleFormState = VehicleFormState(
        type = "motor",
        nama = "Beat Hitam",
        merek = "Honda",
        model = "Beat",
        tahun = "2022",
        platNomor = "B 4521 KZA",
        odometer = "18420",
        warna = "#1A2418",
    ),
    onBack: () -> Unit = {},
    onSave: (VehicleFormState) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var formState by remember { mutableStateOf(initial) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    FormShell(
        title = "Edit Kendaraan",
        onClose = onBack,
        onSave = { onSave(formState) },
        saveEnabled = formState.isValid,
        saveLabel = "Simpan",
        footer = {
            AppButton(
                text = "Simpan Perubahan",
                onClick = { onSave(formState) },
                enabled = formState.isValid,
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VehicleForm(
                state = formState,
                onStateChange = { formState = it },
                showTypeSelector = true,
            )
            Spacer(Modifier.height(8.dp))
            DangerZoneCard(
                label = "Hapus kendaraan ini",
                description = "Riwayat servis dan pengingat yang terkait juga akan ikut terhapus.",
                onClick = { showDeleteDialog = true },
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Hapus kendaraan ini?",
            body = "Riwayat servis dan pengingat untuk kendaraan ini akan ikut terhapus. Tindakan ini tidak bisa dibatalkan.",
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

@Preview(showBackground = true)
@Composable
private fun EditVehicleScreenPreview() {
    EditVehicleScreen()
}
