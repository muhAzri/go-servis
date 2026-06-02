package com.zrifapps.goservice.ui.service

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.service.domain.model.ServiceKind
import com.zrifapps.goservice.feature.service.presentation.AddServiceViewModel
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.MultiPicker
import com.zrifapps.goservice.ui.components.MultiPickerGroup
import com.zrifapps.goservice.ui.components.MultiPickerItem
import com.zrifapps.goservice.ui.components.VehicleOption
import com.zrifapps.goservice.ui.components.VehiclePickerRow
import com.zrifapps.goservice.ui.components.VehiclePickerSheet
import com.zrifapps.goservice.ui.components.toVehicleOption
import com.zrifapps.goservice.ui.service.components.AddServiceContext
import com.zrifapps.goservice.ui.service.components.AutoReminderInfoCard
import com.zrifapps.goservice.ui.service.components.ComponentChipsRow
import com.zrifapps.goservice.ui.service.components.DateField
import com.zrifapps.goservice.ui.service.components.EditableField
import com.zrifapps.goservice.ui.service.components.FieldLabel
import com.zrifapps.goservice.ui.service.components.ServiceDatePickerDialog
import com.zrifapps.goservice.ui.service.components.toMultiPickerItem
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddServiceScreen(
    onClose: () -> Unit,
    onSaved: (String) -> Unit = {},
    vehicleId: String? = null,
    sourceReminderId: String? = null,
    trackedComponentId: String? = null,
    vm: AddServiceViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    LaunchedEffect(vehicleId, sourceReminderId, trackedComponentId) {
        vm.preselect(vehicleId, sourceReminderId, trackedComponentId)
    }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is AddServiceViewModel.Event.Saved -> onSaved(event.recordId)
                is AddServiceViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
    }

    val context = when {
        sourceReminderId != null -> AddServiceContext.FromReminder
        trackedComponentId != null -> AddServiceContext.FromComponent
        else -> AddServiceContext.Manual
    }
    var contextDismissed by remember { mutableStateOf(false) }
    val contextActive = !contextDismissed && context != AddServiceContext.Manual

    val vehicleOptions = remember(state.vehicles) { state.vehicles.map { it.toVehicleOption() } }
    val selectedOption: VehicleOption? = remember(vehicleOptions, state.selectedVehicleId) {
        vehicleOptions.firstOrNull { it.id == state.selectedVehicleId }
            ?: vehicleOptions.firstOrNull()
    }
    val componentItems: List<MultiPickerItem> = remember(state.availableComponents) {
        state.availableComponents.map { it.toMultiPickerItem() }
    }
    val kmText = state.odometerKm?.toString().orEmpty()
    val costText = state.costIdr.takeIf { it > 0L }?.toString().orEmpty()

    var showVehicleSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showComponentPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TopBar(onClose = onClose)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(vertical = 8.dp),
        ) {
            if (contextActive) {
                ContextBanner(
                    title = if (context == AddServiceContext.FromReminder) "Dari reminder" else "Untuk komponen yang dipantau",
                    body = if (context == AddServiceContext.FromReminder)
                        "Reminder akan otomatis ditandai selesai setelah kamu simpan."
                    else
                        "Servis ini akan tercatat sebagai update komponen yang dipantau.",
                    icon = if (context == AddServiceContext.FromReminder) FaIcons.BELL else FaIcons.WRENCH,
                    tone = ContextBannerTone.Info,
                    onDismiss = { contextDismissed = true },
                )
                Spacer(Modifier.height(14.dp))
            }
            FieldLabel("Kendaraan")
            if (selectedOption != null) {
                VehiclePickerRow(
                    selected = selectedOption,
                    locked = contextActive,
                    onClick = { if (!contextActive) showVehicleSheet = true },
                )
            }
            Spacer(Modifier.height(18.dp))

            FieldLabel("Jenis catatan")
            ModeSelector(
                selected = state.mode,
                onSelect = { vm.setMode(it) },
            )
            Spacer(Modifier.height(18.dp))

            when (state.mode) {
                ServiceKind.Komponen -> {
                    FieldLabel("Komponen yang diservis · ${state.selectedComponentIds.size}")
                    if (state.hasTrackedComponents) {
                        ComponentChipsRow(
                            selectedIds = state.selectedComponentIds,
                            allItems = componentItems,
                            onRemove = { id -> vm.toggleComponent(id) },
                            onAdd = { showComponentPicker = true },
                        )
                        Text(
                            text = "Daftar diambil dari komponen yang kamu pantau. Reminder berikutnya dibuat otomatis per komponen.",
                            color = AppColors.TextSubtle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = plusJakartaSansFontFamily(),
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
                        )
                    } else {
                        ContextBanner(
                            title = "Belum ada komponen dipantau",
                            body = "Tambah komponen di Detail Kendaraan → Komponen dulu, atau pakai mode Rutin / Manual.",
                            icon = FaIcons.WRENCH,
                            tone = ContextBannerTone.Warning,
                        )
                        Spacer(Modifier.height(18.dp))
                    }
                }
                ServiceKind.Rutin -> {
                    val rutinKm = if (state.selectedVehicle?.type?.name == "Mobil") "10.000" else "4.000"
                    ContextBanner(
                        title = "Servis Rutin / Berkala",
                        body = "Cocok untuk servis berkala umum di bengkel (tidak tahu komponen apa saja yang diganti). Kami buat reminder otomatis ~6 bulan / $rutinKm km kedepan.",
                        icon = FaIcons.CIRCLE_INFO,
                        tone = ContextBannerTone.Info,
                    )
                    Spacer(Modifier.height(18.dp))
                }
                ServiceKind.Manual -> {
                    EditableField(
                        label = "Apa yang diservis?",
                        value = state.customTitle,
                        onValueChange = vm::setCustomTitle,
                        placeholder = "cth. Ganti spion, jok baru, klakson",
                        icon = FaIcons.WRENCH,
                    )
                    Text(
                        text = "Sekali catat, tidak dibuat reminder otomatis. Cocok untuk perbaikan satu kali.",
                        color = AppColors.TextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = plusJakartaSansFontFamily(),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 18.dp),
                    )
                }
            }

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

            if (state.mode != ServiceKind.Manual) {
                Spacer(Modifier.height(4.dp))
                AutoReminderInfoCard()
            }
            Spacer(Modifier.height(20.dp))
        }

        SaveBar(onSave = { vm.submit() }, enabled = state.canSave)
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
            subtitle = "${state.selectedComponentIds.size} terpilih dari ${componentItems.size} dipantau",
            groups = listOf(MultiPickerGroup(title = "Dipantau", items = componentItems)),
            initiallySelected = state.selectedComponentIds,
            onDismiss = { showComponentPicker = false },
            onConfirm = { picked ->
                vm.setComponents(picked)
                showComponentPicker = false
            },
        )
    }
}

@Composable
private fun TopBar(onClose: () -> Unit) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CircleIconButton(icon = FaIcons.XMARK, onClick = onClose)
        Text(
            text = "Catat Servis",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun ModeSelector(
    selected: ServiceKind,
    onSelect: (ServiceKind) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val options = listOf(
        ServiceKind.Komponen to "Komponen",
        ServiceKind.Rutin to "Rutin",
        ServiceKind.Manual to "Manual",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (mode, label) ->
            val active = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) AppColors.PrimarySoft else AppColors.Surface)
                    .border(
                        BorderStroke(1.5.dp, if (active) AppColors.Primary else AppColors.Border),
                        RoundedCornerShape(12.dp),
                    )
                    .clickable { onSelect(mode) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (active) AppColors.Primary else AppColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun SaveBar(onSave: () -> Unit, enabled: Boolean = true) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        AppButton(text = "Simpan Servis", onClick = onSave, enabled = enabled)
    }
}
