package com.zrifapps.goservice.ui.service

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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.service.presentation.EditServiceViewModel
import com.zrifapps.goservice.ui.components.AppBackButton
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.MultiPicker
import com.zrifapps.goservice.ui.components.MultiPickerGroup
import com.zrifapps.goservice.ui.components.VehicleOption
import com.zrifapps.goservice.ui.components.VehiclePickerRow
import com.zrifapps.goservice.ui.components.VehiclePickerSheet
import com.zrifapps.goservice.ui.components.toVehicleOption
import com.zrifapps.goservice.ui.service.components.AutoReminderInfoCard
import com.zrifapps.goservice.ui.service.components.ComponentChipsRow
import com.zrifapps.goservice.ui.service.components.DateField
import com.zrifapps.goservice.ui.service.components.EditableField
import com.zrifapps.goservice.ui.service.components.FieldLabel
import com.zrifapps.goservice.ui.service.components.ServiceDatePickerDialog
import com.zrifapps.goservice.ui.service.components.ServiceTypeGrid
import com.zrifapps.goservice.ui.service.components.trackedComponents
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditServiceScreen(
    recordId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    vm: EditServiceViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(recordId) { vm.load(recordId) }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is EditServiceViewModel.Event.Saved -> onSaved()
                is EditServiceViewModel.Event.Deleted -> onDeleted()
                is EditServiceViewModel.Event.Failed -> Unit
            }
        }
    }

    val vehicleOptions = remember(state.vehicles) { state.vehicles.map { it.toVehicleOption() } }
    val selectedOption: VehicleOption? = remember(vehicleOptions, state.selectedVehicleId) {
        vehicleOptions.firstOrNull { it.id == state.selectedVehicleId }
    }
    val kmText = state.odometerKm?.toString().orEmpty()
    val costText = state.costIdr.takeIf { it > 0L }?.toString().orEmpty()

    var showVehicleSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showComponentPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppBackButton(onClick = onBack)
            Text(
                text = "Edit Servis",
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = plusJakartaSansFontFamily(),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(vertical = 8.dp),
        ) {
            FieldLabel("Kendaraan")
            if (selectedOption != null) {
                VehiclePickerRow(
                    selected = selectedOption,
                    onClick = { showVehicleSheet = true },
                )
            }
            Spacer(Modifier.height(18.dp))

            FieldLabel("Jenis servis")
            ServiceTypeGrid(
                selected = state.serviceType.key,
                onSelect = { key -> vm.setServiceType(ServiceType.fromKey(key)) },
            )
            Spacer(Modifier.height(18.dp))

            FieldLabel("Komponen yang diservis · ${state.selectedComponentIds.size}")
            ComponentChipsRow(
                selectedIds = state.selectedComponentIds,
                allItems = trackedComponents,
                onRemove = vm::toggleComponent,
                onAdd = { showComponentPicker = true },
            )
            Spacer(Modifier.height(18.dp))

            DateField(
                label = "Tanggal servis",
                dateMillis = state.serviceDateMillis,
                onClick = { showDatePicker = true },
            )
            EditableField(
                label = "KM saat servis",
                value = kmText,
                onValueChange = { text -> vm.setOdometer(text.filter { it.isDigit() }.toLongOrNull()) },
                placeholder = "cth. 18420",
                icon = FaIcons.GAUGE,
                keyboardType = KeyboardType.Number,
                monospaced = true,
            )
            EditableField(
                label = "Bengkel",
                value = state.workshop,
                onValueChange = vm::setWorkshop,
                placeholder = "cth. AHASS Kebon Jeruk",
                icon = FaIcons.LOCATION_DOT,
            )
            EditableField(
                label = "Biaya",
                value = costText,
                onValueChange = { text -> vm.setCost(text.filter { it.isDigit() }.toLongOrNull() ?: 0L) },
                placeholder = "cth. 65000",
                icon = null,
                keyboardType = KeyboardType.Number,
                monospaced = true,
                prefix = "Rp ",
            )
            EditableField(
                label = "Catatan",
                value = state.note,
                onValueChange = vm::setNote,
                placeholder = "cth. AHM MPX2 0.8L",
                icon = null,
                singleLine = false,
                imeAction = ImeAction.Default,
            )
            Spacer(Modifier.height(4.dp))
            AutoReminderInfoCard()
            Spacer(Modifier.height(20.dp))

            DeleteButton(onClick = { showDeleteDialog = true })
            Spacer(Modifier.height(24.dp))
        }

        SaveBar(onSave = { vm.save() }, enabled = state.canSave)
    }

    if (showVehicleSheet && selectedOption != null) {
        VehiclePickerSheet(
            options = vehicleOptions,
            selectedId = selectedOption.id,
            onDismiss = { showVehicleSheet = false },
            onPick = { picked -> vm.selectVehicle(picked.id) },
        )
    }

    if (showDatePicker) {
        ServiceDatePickerDialog(
            initialMillis = state.serviceDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                vm.setServiceDate(millis)
                showDatePicker = false
            },
        )
    }

    if (showComponentPicker) {
        MultiPicker(
            title = "Pilih komponen yang diservis",
            subtitle = "${state.selectedComponentIds.size} terpilih dari ${trackedComponents.size} dipantau",
            groups = listOf(MultiPickerGroup(title = "Dipantau", items = trackedComponents)),
            initiallySelected = state.selectedComponentIds,
            onDismiss = { showComponentPicker = false },
            onConfirm = { picked ->
                vm.setComponents(picked)
                showComponentPicker = false
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus servis?") },
            text = { Text("Catatan servis ini akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.delete()
                }) {
                    Text("Hapus", color = AppColors.Danger)
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
private fun DeleteButton(onClick: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Danger.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.TRASH, color = AppColors.Danger, size = 13.sp)
            Text(
                text = "Hapus servis",
                color = AppColors.Danger,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@Composable
private fun SaveBar(onSave: () -> Unit, enabled: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        AppButton(text = "Simpan Perubahan", onClick = onSave, enabled = enabled)
    }
}
