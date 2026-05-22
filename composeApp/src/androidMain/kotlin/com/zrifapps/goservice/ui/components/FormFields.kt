package com.zrifapps.goservice.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

@Composable
fun PHLabel(
    text: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text.uppercase(),
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            fontFamily = font,
        )
        if (hint != null) {
            Text(
                text = hint,
                color = AppColors.TextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )
        }
    }
}

@Composable
fun PHInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: String? = null,
    suffix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    singleLine: Boolean = true,
) {
    val font = plusJakartaSansFontFamily()
    var focused by remember { mutableStateOf(false) }
    val borderColor = if (focused) AppColors.Primary else AppColors.Border

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        cursorBrush = SolidColor(AppColors.Primary),
        textStyle = TextStyle(
            color = AppColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onAny = { onImeAction() }),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Surface)
                    .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (leadingIcon != null) {
                    FaIcon(icon = leadingIcon, color = AppColors.TextSubtle, size = 14.sp)
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
                    innerTextField()
                }
                if (suffix != null) {
                    Text(
                        text = suffix,
                        color = AppColors.TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = font,
                    )
                }
            }
        },
    )
}

@Composable
fun PHPickerRow(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pilih…",
    leadingIcon: String? = null,
    enabled: Boolean = true,
) {
    val font = plusJakartaSansFontFamily()
    val isEmpty = value.isEmpty()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) AppColors.Surface else AppColors.SurfaceAlt)
            .border(1.5.dp, AppColors.Border, RoundedCornerShape(14.dp))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (leadingIcon != null) {
            FaIcon(icon = leadingIcon, color = AppColors.TextSubtle, size = 14.sp)
        }
        Text(
            text = if (isEmpty) placeholder else value,
            color = if (isEmpty) AppColors.TextSubtle else AppColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = if (isEmpty) FontWeight.Medium else FontWeight.SemiBold,
            fontFamily = font,
            modifier = Modifier.weight(1f),
        )
        FaIcon(icon = FaIcons.CHEVRON_DOWN, color = AppColors.TextSubtle, size = 12.sp)
    }
}

data class PHSegmentedOption(val value: String, val label: String, val icon: String? = null)

@Composable
fun PHSegmented(
    options: List<PHSegmentedOption>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.SurfaceAlt)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { opt ->
            val active = opt.value == selected
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (active) AppColors.Surface else Color.Transparent)
                    .clickable { onSelect(opt.value) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (opt.icon != null) {
                    FaIcon(
                        icon = opt.icon,
                        color = if (active) AppColors.Primary else AppColors.TextMuted,
                        size = 12.sp,
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    text = opt.label,
                    color = if (active) AppColors.TextPrimary else AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
                    fontFamily = font,
                )
            }
        }
    }
}
