package com.zrifapps.goservice.ui.reminders.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun ReminderFieldLabel(text: String) {
    val font = plusJakartaSansFontFamily()
    Text(
        text = text.uppercase(),
        color = AppColors.TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        fontFamily = font,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
internal fun TriggerSegmented(
    selected: ReminderTriggerMode,
    onSelect: (ReminderTriggerMode) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val options = listOf(
        ReminderTriggerMode.Km to "Per KM",
        ReminderTriggerMode.Date to "Per Tanggal",
        ReminderTriggerMode.Both to "Keduanya",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.SurfaceAlt)
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

@Composable
internal fun KmInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    helper: String,
) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border

    Column {
        ReminderFieldLabel(label)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(AppColors.Primary),
            textStyle = TextStyle(
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
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
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    FaIcon(icon = FaIcons.GAUGE, color = AppColors.TextMuted, size = 16.sp)
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = "cth. 20420",
                                color = AppColors.TextSubtle,
                                fontSize = 15.sp,
                                fontFamily = font,
                            )
                        }
                        inner()
                    }
                    Text(
                        text = "km",
                        color = AppColors.TextSubtle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                    )
                }
            },
        )
        if (helper.isNotEmpty()) {
            Text(
                text = helper,
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
            )
        }
    }
}

@Composable
internal fun DateInputField(
    label: String,
    dateMillis: Long,
    onTap: () -> Unit,
    helper: String,
) {
    val font = plusJakartaSansFontFamily()
    val formatter = remember { SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID")) }
    val display = remember(dateMillis) {
        if (dateMillis > 0L) formatter.format(Date(dateMillis)) else "Pilih tanggal"
    }

    Column {
        ReminderFieldLabel(label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppColors.Surface)
                .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
                .clickable(onClick = onTap)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FaIcon(icon = FaIcons.CALENDAR, color = AppColors.TextMuted, size = 16.sp)
            Text(
                text = display,
                color = AppColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                modifier = Modifier.weight(1f),
            )
            FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 12.sp)
        }
        if (helper.isNotEmpty()) {
            Text(
                text = helper,
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
            )
        }
    }
}

@Composable
internal fun TitleInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(AppColors.Primary),
        textStyle = TextStyle(
            color = AppColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = AppColors.TextSubtle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font,
                    )
                }
                inner()
            }
        },
    )
}

@Composable
internal fun NoteInputField(value: String, onValueChange: (String) -> Unit) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = false,
        cursorBrush = SolidColor(AppColors.Primary),
        textStyle = TextStyle(
            color = AppColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
            lineHeight = 20.sp,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Tambah catatan…",
                        color = AppColors.TextSubtle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font,
                        lineHeight = 20.sp,
                    )
                }
                inner()
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReminderDatePickerDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    )
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
