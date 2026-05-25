package com.zrifapps.goservice.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.presentation.EditVehicleViewModel
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.ConfirmDialog
import com.zrifapps.goservice.ui.components.ConfirmDialogTone
import com.zrifapps.goservice.ui.components.DangerZoneCard
import com.zrifapps.goservice.ui.components.FormShell
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.vehicle.components.VehicleForm
import com.zrifapps.goservice.ui.vehicle.components.VehicleFormState
import com.zrifapps.goservice.ui.vehicle.components.toOnboardingInput
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditVehicleScreen(
    vehicleId: String? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    onDeleted: () -> Unit = {},
    vm: EditVehicleViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vehicleId) { vm.load(vehicleId) }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is EditVehicleViewModel.Event.Saved -> onSaved()
                is EditVehicleViewModel.Event.Deleted -> onDeleted()
                is EditVehicleViewModel.Event.Failed -> Unit
            }
        }
    }

    val hydratedVehicleId = state.vehicle?.id
    var formState by remember(hydratedVehicleId) {
        mutableStateOf(state.vehicle?.toFormState() ?: VehicleFormState())
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    FormShell(
        title = "Edit Kendaraan",
        onClose = onBack,
        onSave = { vm.save(formState.toOnboardingInput()) },
        saveEnabled = formState.isValid && !state.isLoading && !state.isSaving && state.vehicle != null,
        saveLabel = "Simpan",
        footer = {
            AppButton(
                text = "Simpan Perubahan",
                onClick = { vm.save(formState.toOnboardingInput()) },
                enabled = formState.isValid && !state.isLoading && !state.isSaving && state.vehicle != null,
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
                vm.delete()
            },
        )
    }
}

private fun Vehicle.toFormState(): VehicleFormState = VehicleFormState(
    type = type.key,
    nama = nickname,
    merek = brand,
    model = model,
    tahun = year?.toString().orEmpty(),
    platNomor = plateNumber,
    odometer = odometer.kilometers.toString(),
    warna = color.value,
)
