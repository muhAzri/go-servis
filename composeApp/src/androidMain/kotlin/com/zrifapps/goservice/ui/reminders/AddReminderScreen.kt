package com.zrifapps.goservice.ui.reminders

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.presentation.AddReminderViewModel
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.VehicleOption
import com.zrifapps.goservice.ui.components.VehiclePickerRow
import com.zrifapps.goservice.ui.components.VehiclePickerSheet
import com.zrifapps.goservice.ui.components.toVehicleOption
import com.zrifapps.goservice.ui.reminders.components.DateInputField
import com.zrifapps.goservice.ui.reminders.components.KmInputField
import com.zrifapps.goservice.ui.reminders.components.NoteInputField
import com.zrifapps.goservice.ui.reminders.components.ReminderDatePickerDialog
import com.zrifapps.goservice.ui.reminders.components.ReminderFieldLabel
import com.zrifapps.goservice.ui.reminders.components.TriggerSegmented
import com.zrifapps.goservice.ui.service.components.ServiceTypeGrid
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddReminderScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit = {},
    vehicleId: String? = null,
    trackedComponentId: String? = null,
    fromContext: Boolean = false,
    vm: AddReminderViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(vehicleId, trackedComponentId) {
        vm.preselect(vehicleId, trackedComponentId)
    }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            if (event is AddReminderViewModel.Event.Saved) onSaved()
        }
    }

    val vehicleOptions = remember(state.vehicles) { state.vehicles.map { it.toVehicleOption() } }
    val selectedOption: VehicleOption? = remember(vehicleOptions, state.selectedVehicleId) {
        vehicleOptions.firstOrNull { it.id == state.selectedVehicleId }
    }
    val kmText = state.targetKm?.toString().orEmpty()

    var showVehicleSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCtxBanner by remember { mutableStateOf(fromContext || vehicleId != null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(
            onClose = onClose,
            onSave = { vm.submit() },
            saveEnabled = state.canSave,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            if (showCtxBanner) {
                ContextBanner(
                    title = "Dari konteks",
                    body = "Reminder ini terhubung ke kendaraan/komponen yang dipilih.",
                    icon = FaIcons.BELL,
                    tone = ContextBannerTone.Info,
                    onDismiss = { showCtxBanner = false },
                )
                Spacer(Modifier.height(16.dp))
            }

            ReminderFieldLabel("Kendaraan")
            if (selectedOption != null) {
                VehiclePickerRow(
                    selected = selectedOption,
                    locked = vehicleId != null,
                    onClick = { if (vehicleId == null) showVehicleSheet = true },
                )
            }
            Spacer(Modifier.height(18.dp))

            ReminderFieldLabel("Jenis servis")
            ServiceTypeGrid(
                selected = state.serviceType.key,
                onSelect = { key -> vm.setServiceType(ServiceType.fromKey(key)) },
            )
            Spacer(Modifier.height(18.dp))

            ReminderFieldLabel("Picu pengingat")
            TriggerSegmented(selected = state.triggerMode, onSelect = vm::setTriggerMode)
            Spacer(Modifier.height(14.dp))

            if (state.triggerMode == ReminderTriggerMode.Km || state.triggerMode == ReminderTriggerMode.Both) {
                val currentKm = selectedOption?.let { _ ->
                    state.selectedVehicle?.odometer?.kilometers
                }
                KmInputField(
                    label = "Target KM",
                    value = kmText,
                    onValueChange = { text -> vm.setTargetKm(text.filter { it.isDigit() }.toLongOrNull()) },
                    helper = if (currentKm != null) "KM saat ini ${formatKm(currentKm)}" else "",
                )
            }
            if (state.triggerMode == ReminderTriggerMode.Date || state.triggerMode == ReminderTriggerMode.Both) {
                Spacer(Modifier.height(12.dp))
                DateInputField(
                    label = "Tanggal",
                    dateMillis = state.targetDateMillis,
                    onTap = { showDatePicker = true },
                    helper = "Notif mulai ${state.notifyDaysBefore} hari sebelumnya",
                )
            }
            Spacer(Modifier.height(18.dp))

            ReminderFieldLabel("Catatan (opsional)")
            NoteInputField(value = state.note, onValueChange = vm::setNote)
            Spacer(Modifier.height(24.dp))
        }

        BottomSaveBar(onSave = { vm.submit() }, enabled = state.canSave)
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
        ReminderDatePickerDialog(
            initialMillis = state.targetDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                vm.setTargetDate(millis)
                showDatePicker = false
            },
        )
    }
}

@Composable
private fun TopBar(onClose: () -> Unit, onSave: () -> Unit, saveEnabled: Boolean) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleIconButton(icon = FaIcons.XMARK, onClick = onClose)
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Buat Pengingat",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "Simpan",
            color = if (saveEnabled) AppColors.Primary else AppColors.TextSubtle,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = saveEnabled, onClick = onSave)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun BottomSaveBar(onSave: () -> Unit, enabled: Boolean) {
    val font = plusJakartaSansFontFamily()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (enabled) AppColors.Primary else AppColors.Primary.copy(alpha = 0.4f))
                .clickable(enabled = enabled, onClick = onSave),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Simpan Pengingat",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

private fun formatKm(km: Long): String {
    if (km == 0L) return "0 km"
    val abs = kotlin.math.abs(km).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return "$grouped km"
}
