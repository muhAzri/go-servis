package com.zrifapps.goservice.ui.service.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import com.zrifapps.goservice.feature.service.presentation.AddServiceViewModel
import com.zrifapps.goservice.feature.service.presentation.EditServiceViewModel
import com.zrifapps.goservice.ui.components.MultiPickerItem
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AddServiceContext { Manual, FromReminder, FromComponent }

internal data class ServiceTypeChoice(
    val id: String,
    val label: String,
    val icon: String,
    val color: Color,
)

internal val serviceTypes = listOf(
    ServiceTypeChoice("oli",      "Ganti Oli\nMesin",    FaIcons.OIL_CAN,         Color(0xFFE89C2E)),
    ServiceTypeChoice("filter",   "Filter Oli\n& Udara", FaIcons.FILTER,          Color(0xFF7B6FE8)),
    ServiceTypeChoice("ban",      "Rotasi/\nGanti Ban",  FaIcons.LIFE_RING,       Color(0xFF3F4D5C)),
    ServiceTypeChoice("aki",      "Aki",                  FaIcons.CAR_BATTERY,     Color(0xFFD6453A)),
    ServiceTypeChoice("rem",      "Kampas\nRem",         FaIcons.CIRCLE_NOTCH,    Color(0xFF2E8B57)),
    ServiceTypeChoice("radiator", "Radiator/\nCoolant",  FaIcons.TEMPERATURE_HALF,Color(0xFF3FB1D6)),
    ServiceTypeChoice("tune_up",  "Tune-up",              FaIcons.BOLT,            Color(0xFFE8B62E)),
    ServiceTypeChoice("other",    "Lainnya",              FaIcons.WRENCH,          AppColors.Primary),
)

internal fun serviceTypeMeta(key: String): ServiceTypeChoice =
    serviceTypes.firstOrNull { it.id == key } ?: serviceTypes.last()

internal val trackedComponents = listOf(
    MultiPickerItem(id = "oli_mesin",   label = "Oli mesin",     subtitle = "2.000 km", icon = FaIcons.OIL_CAN,  color = Color(0xFFE89C2E)),
    MultiPickerItem(id = "filter_oli",  label = "Filter oli",    subtitle = "4.000 km", icon = FaIcons.FILTER,   color = Color(0xFF7B6FE8)),
    MultiPickerItem(id = "filter_udara",label = "Filter udara",  subtitle = "8.000 km", icon = FaIcons.FILTER,   color = Color(0xFF7B6FE8)),
    MultiPickerItem(id = "busi",        label = "Busi & tune-up",subtitle = "6.000 km", icon = FaIcons.BOLT,     color = Color(0xFFE8B62E)),
    MultiPickerItem(id = "aki",         label = "Aki",            subtitle = "1–2 tahun", icon = FaIcons.CAR_BATTERY, color = Color(0xFFD6453A)),
    MultiPickerItem(id = "kampas_rem",  label = "Kampas rem",     subtitle = "8.000 km", icon = FaIcons.CIRCLE_NOTCH, color = AppColors.Primary),
    MultiPickerItem(id = "ban",         label = "Ban",            subtitle = "10.000 km", icon = FaIcons.LIFE_RING, color = Color(0xFF3F4D5C)),
    MultiPickerItem(id = "radiator",    label = "Radiator / coolant", subtitle = "tahunan", icon = FaIcons.TEMPERATURE_HALF, color = Color(0xFF3FB1D6)),
)

internal fun trackedComponentMeta(id: String): MultiPickerItem? =
    trackedComponents.firstOrNull { it.id == id }

internal fun AddServiceViewModel.ComponentOption.toMultiPickerItem(): MultiPickerItem =
    MultiPickerItem(
        id = id,
        label = label,
        subtitle = intervalLabel,
        icon = iconKeyToFa(iconKey),
        color = parseHexColor(colorHex),
    )

internal fun EditServiceViewModel.ComponentOption.toMultiPickerItem(): MultiPickerItem =
    MultiPickerItem(
        id = id,
        label = label,
        subtitle = intervalLabel,
        icon = iconKeyToFa(iconKey),
        color = parseHexColor(colorHex),
    )

private fun iconKeyToFa(key: String): String = when (key) {
    "OIL_CAN" -> FaIcons.OIL_CAN
    "BOLT" -> FaIcons.BOLT
    "CAR_BATTERY" -> FaIcons.CAR_BATTERY
    "CIRCLE_NOTCH" -> FaIcons.CIRCLE_NOTCH
    "LIFE_RING" -> FaIcons.LIFE_RING
    "FILTER" -> FaIcons.FILTER
    "TEMPERATURE_HALF" -> FaIcons.TEMPERATURE_HALF
    "GEAR" -> FaIcons.GEAR
    "GEARS" -> FaIcons.GEARS
    "WRENCH" -> FaIcons.WRENCH
    else -> FaIcons.WRENCH
}

private fun parseHexColor(hex: String, fallback: Color = AppColors.Primary): Color =
    try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Throwable) {
        fallback
    }

internal fun formatServiceDate(millis: Long): String {
    if (millis <= 0L) return "—"
    val formatter = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))
    return formatter.format(Date(millis))
}

internal fun formatRupiah(amountIdr: Long): String = "Rp ${formatGrouped(amountIdr)}"

internal fun formatKmDisplay(km: Long): String = "${formatGrouped(km)} km"

private fun formatGrouped(value: Long): String {
    if (value == 0L) return "0"
    val abs = kotlin.math.abs(value).toString()
    val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
    return if (value < 0) "-$grouped" else grouped
}

@Composable
internal fun FieldLabel(text: String) {
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
internal fun ComponentChipsRow(
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
            FlowRow(
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
internal fun ServiceDatePickerDialog(
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
internal fun EditableField(
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
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
                        .padding(horizontal = 16.dp, vertical = 14.dp),
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
internal fun DateField(
    label: String,
    dateMillis: Long,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val displayDate = remember(dateMillis) { formatServiceDate(dateMillis) }

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
internal fun AutoReminderInfoCard() {
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
            text = "Berdasarkan interval pabrikan untuk komponen yang dipilih.",
            color = AppColors.TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
