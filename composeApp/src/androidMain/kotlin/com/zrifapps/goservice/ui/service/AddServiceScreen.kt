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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.components.ContextBanner
import com.zrifapps.goservice.ui.components.ContextBannerTone
import com.zrifapps.goservice.ui.components.DEFAULT_VEHICLE_OPTIONS
import com.zrifapps.goservice.ui.components.MultiPicker
import com.zrifapps.goservice.ui.components.MultiPickerGroup
import com.zrifapps.goservice.ui.components.MultiPickerItem
import com.zrifapps.goservice.ui.components.VehiclePickerRow
import com.zrifapps.goservice.ui.components.VehiclePickerSheet
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ServiceTypeChoice(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color,
)

private val serviceTypes = listOf(
    ServiceTypeChoice("oli",      "Ganti Oli\nMesin",    FaIcons.OIL_CAN,         Color(0xFFE89C2E)),
    ServiceTypeChoice("filter",   "Filter Oli\n& Udara", FaIcons.FILTER,          Color(0xFF7B6FE8)),
    ServiceTypeChoice("ban",      "Rotasi/\nGanti Ban",  FaIcons.LIFE_RING,       Color(0xFF3F4D5C)),
    ServiceTypeChoice("aki",      "Aki",                  FaIcons.CAR_BATTERY,     Color(0xFFD6453A)),
    ServiceTypeChoice("rem",      "Kampas\nRem",         FaIcons.CIRCLE_NOTCH,    Color(0xFF2E8B57)),
    ServiceTypeChoice("radiator", "Radiator/\nCoolant",  FaIcons.TEMPERATURE_HALF,Color(0xFF3FB1D6)),
)

enum class AddServiceContext { Manual, FromReminder, FromComponent }

private val trackedComponents = listOf(
    MultiPickerItem(id = "oli_mesin",   label = "Oli mesin",     subtitle = "2.000 km", icon = FaIcons.OIL_CAN,  color = Color(0xFFE89C2E)),
    MultiPickerItem(id = "filter_oli",  label = "Filter oli",    subtitle = "4.000 km", icon = FaIcons.FILTER,   color = Color(0xFF7B6FE8)),
    MultiPickerItem(id = "filter_udara",label = "Filter udara",  subtitle = "8.000 km", icon = FaIcons.FILTER,   color = Color(0xFF7B6FE8)),
    MultiPickerItem(id = "busi",        label = "Busi & tune-up",subtitle = "6.000 km", icon = FaIcons.BOLT,     color = Color(0xFFE8B62E)),
    MultiPickerItem(id = "aki",         label = "Aki",            subtitle = "1–2 tahun", icon = FaIcons.CAR_BATTERY, color = Color(0xFFD6453A)),
    MultiPickerItem(id = "kampas_rem",  label = "Kampas rem",     subtitle = "8.000 km", icon = FaIcons.CIRCLE_NOTCH, color = AppColors.Primary),
    MultiPickerItem(id = "ban",         label = "Ban",            subtitle = "10.000 km", icon = FaIcons.LIFE_RING, color = Color(0xFF3F4D5C)),
    MultiPickerItem(id = "radiator",    label = "Radiator / coolant", subtitle = "tahunan", icon = FaIcons.TEMPERATURE_HALF, color = Color(0xFF3FB1D6)),
)

@Composable
fun AddServiceScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit = {},
    context: AddServiceContext = AddServiceContext.Manual,
) {
    val vehicles = remember { DEFAULT_VEHICLE_OPTIONS }
    var selectedVehicle by remember { mutableStateOf(vehicles.first()) }
    var selectedType by remember { mutableStateOf("oli") }
    var serviceDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var kmText by remember { mutableStateOf("") }
    var workshop by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedComponents by remember { mutableStateOf(setOf("oli_mesin", "filter_oli")) }

    var showVehicleSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showComponentPicker by remember { mutableStateOf(false) }
    var contextDismissed by remember { mutableStateOf(false) }
    val contextActive = !contextDismissed && context != AddServiceContext.Manual

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
                    title = when (context) {
                        AddServiceContext.FromReminder -> "Dari reminder"
                        AddServiceContext.FromComponent -> "Untuk komponen: Oli mesin"
                        else -> ""
                    },
                    body = when (context) {
                        AddServiceContext.FromReminder -> "Ganti Oli Mesin · Beat Hitam — field di bawah sudah diisi otomatis."
                        AddServiceContext.FromComponent -> "Servis ini akan tercatat sebagai update komponen yang dipantau."
                        else -> ""
                    },
                    icon = if (context == AddServiceContext.FromReminder) FaIcons.BELL else FaIcons.WRENCH,
                    tone = ContextBannerTone.Info,
                    onDismiss = { contextDismissed = true },
                )
                Spacer(Modifier.height(14.dp))
            }
            FieldLabel("Kendaraan")
            VehiclePickerRow(
                selected = selectedVehicle,
                locked = contextActive,
                onClick = { if (!contextActive) showVehicleSheet = true },
            )
            Spacer(Modifier.height(18.dp))

            FieldLabel("Jenis servis")
            ServiceTypeGrid(selected = selectedType, onSelect = { if (!contextActive) selectedType = it })
            Spacer(Modifier.height(18.dp))

            FieldLabel("Komponen yang diservis · ${selectedComponents.size}")
            ComponentChipsRow(
                selectedIds = selectedComponents,
                allItems = trackedComponents,
                onRemove = { id -> selectedComponents = selectedComponents - id },
                onAdd = { showComponentPicker = true },
            )
            Text(
                text = "Daftar diambil dari komponen yang kamu pantau. Tambah di Detail Kendaraan → Komponen.",
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = plusJakartaSansFontFamily(),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp),
            )

            DateField(
                label = "Tanggal servis",
                dateMillis = serviceDateMillis,
                onClick = { showDatePicker = true },
            )
            EditableField(
                label = "KM saat servis",
                value = kmText,
                onValueChange = { kmText = it.filter { ch -> ch.isDigit() } },
                placeholder = "cth. 18420",
                icon = FaIcons.GAUGE,
                keyboardType = KeyboardType.Number,
                monospaced = true,
            )
            EditableField(
                label = "Bengkel",
                value = workshop,
                onValueChange = { workshop = it },
                placeholder = "cth. AHASS Kebon Jeruk",
                icon = FaIcons.LOCATION_DOT,
            )
            EditableField(
                label = "Biaya",
                value = costText,
                onValueChange = { costText = it.filter { ch -> ch.isDigit() } },
                placeholder = "cth. 65000",
                icon = null,
                keyboardType = KeyboardType.Number,
                monospaced = true,
                prefix = "Rp ",
            )
            EditableField(
                label = "Catatan",
                value = note,
                onValueChange = { note = it },
                placeholder = "cth. AHM MPX2 0.8L",
                icon = null,
                singleLine = false,
                imeAction = ImeAction.Default,
            )

            Spacer(Modifier.height(4.dp))
            AutoReminderInfoCard()
            Spacer(Modifier.height(20.dp))
        }

        SaveBar(onSave = onSaved)
    }

    if (showVehicleSheet) {
        VehiclePickerSheet(
            options = vehicles,
            selectedId = selectedVehicle.id,
            onDismiss = { showVehicleSheet = false },
            onPick = { selectedVehicle = it },
        )
    }

    if (showDatePicker) {
        ServiceDatePickerDialog(
            initialMillis = serviceDateMillis,
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                serviceDateMillis = millis
                showDatePicker = false
            },
        )
    }

    if (showComponentPicker) {
        MultiPicker(
            title = "Pilih komponen yang diservis",
            subtitle = "${selectedComponents.size} terpilih dari ${trackedComponents.size} dipantau",
            groups = listOf(MultiPickerGroup(title = "Dipantau", items = trackedComponents)),
            initiallySelected = selectedComponents,
            onDismiss = { showComponentPicker = false },
            onConfirm = { picked ->
                selectedComponents = picked
                showComponentPicker = false
            },
        )
    }
}

@Composable
private fun ComponentChipsRow(
    selectedIds: Set<String>,
    allItems: List<MultiPickerItem>,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val pickedItems = allItems.filter { it.id in selectedIds }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .padding(10.dp),
    ) {
        if (pickedItems.isNotEmpty()) {
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                pickedItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(AppColors.PrimarySoft)
                            .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        if (item.icon != null) {
                            FaIcon(icon = item.icon, color = AppColors.Primary, size = 11.sp)
                        }
                        Text(
                            text = item.label,
                            color = AppColors.Primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color.Black.copy(alpha = 0.06f))
                                .clickable { onRemove(item.id) },
                            contentAlignment = Alignment.Center,
                        ) {
                            FaIcon(icon = FaIcons.XMARK, color = AppColors.Primary, size = 9.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier
                .clickable(onClick = onAdd)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.PLUS, color = AppColors.Primary, size = 12.sp)
            Text(
                text = "Pilih komponen (${selectedIds.size} / ${allItems.size} dipantau)",
                color = AppColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceDatePickerDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(state.selectedDateMillis ?: initialMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    ) {
        DatePicker(state = state)
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
private fun FieldLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text,
        color = AppColors.TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = font,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun ServiceTypeGrid(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        serviceTypes.chunked(3).forEach { rowChoices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowChoices.forEach { choice ->
                    ServiceTypeTile(
                        choice = choice,
                        active = choice.id == selected,
                        onClick = { onSelect(choice.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowChoices.size < 3) {
                    repeat(3 - rowChoices.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceTypeTile(
    choice: ServiceTypeChoice,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) AppColors.PrimarySoft else AppColors.Surface)
            .border(
                BorderStroke(
                    1.5.dp,
                    if (active) AppColors.Primary else AppColors.Border,
                ),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FaIcon(
            icon = choice.icon,
            color = if (active) AppColors.Primary else choice.color,
            size = 22.sp,
        )
        Text(
            text = choice.label,
            color = AppColors.TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 12.sp,
        )
    }
}

@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    monospaced: Boolean = false,
    singleLine: Boolean = true,
    prefix: String = "",
) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border
    val displayFont = if (monospaced) FontFamily.Monospace else font

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            cursorBrush = SolidColor(AppColors.Primary),
            textStyle = TextStyle(
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = displayFont,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            decorationBox = { inner ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.Surface)
                        .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(14.dp))
                        .heightIn(min = if (singleLine) 0.dp else 64.dp)
                        .padding(horizontal = 16.dp, vertical = if (singleLine) 14.dp else 14.dp),
                    verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (icon != null) {
                        FaIcon(icon = icon, color = AppColors.TextMuted, size = 16.sp)
                    }
                    if (prefix.isNotEmpty() && value.isNotEmpty()) {
                        Text(
                            text = prefix,
                            color = AppColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = displayFont,
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = AppColors.TextSubtle,
                                fontSize = 15.sp,
                                fontFamily = font,
                            )
                        }
                        inner()
                    }
                }
            },
        )
    }
}

@Composable
private fun DateField(
    label: String,
    dateMillis: Long,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val formatter = remember { SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")) }
    val displayDate = remember(dateMillis) { formatter.format(Date(dateMillis)) }

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = FaIcons.CALENDAR, color = AppColors.TextMuted, size = 16.sp)
            Text(
                text = displayDate,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 12.sp)
        }
    }
}

@Composable
private fun AutoReminderInfoCard() {
    val font = plusJakartaSansFontFamily()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.PrimarySofter)
            .border(BorderStroke(1.dp, AppColors.Primary.copy(alpha = 0.19f)), RoundedCornerShape(14.dp))
            .padding(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FaIcon(icon = FaIcons.CIRCLE_INFO, color = AppColors.Primary, size = 14.sp)
            Text(
                text = "Pengingat berikutnya akan diset otomatis",
                color = AppColors.Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
        }
        Text(
            text = "Berdasarkan interval pabrikan: target ganti oli berikutnya 20.420 km atau 6 Juli 2026.",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun SaveBar(onSave: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.Surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
    ) {
        AppButton(text = "Simpan Servis", onClick = onSave)
    }
}
