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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

enum class ActionSheetTone { Default, Danger }
enum class ActionSheetSelectionMode { Tap, Radio }

data class ActionSheetOption(
    val value: String,
    val label: String,
    val subtitle: String? = null,
    val icon: String? = null,
    val tone: ActionSheetTone = ActionSheetTone.Default,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionSheet(
    title: String,
    options: List<ActionSheetOption>,
    onDismiss: () -> Unit,
    onSelect: (ActionSheetOption) -> Unit,
    subtitle: String? = null,
    selectionMode: ActionSheetSelectionMode = ActionSheetSelectionMode.Tap,
    initiallySelected: String? = null,
    primaryLabel: String? = null,
    onPrimary: ((String) -> Unit)? = null,
    cancelLabel: String = "Batal",
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var radioValue by remember { mutableStateOf(initiallySelected) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppColors.Border),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = AppColors.TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    lineHeight = 18.sp,
                )
            }
            Spacer(Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { opt ->
                    ActionSheetRow(
                        option = opt,
                        selected = when (selectionMode) {
                            ActionSheetSelectionMode.Radio -> radioValue == opt.value
                            ActionSheetSelectionMode.Tap -> false
                        },
                        showRadio = selectionMode == ActionSheetSelectionMode.Radio,
                        onClick = {
                            when (selectionMode) {
                                ActionSheetSelectionMode.Radio -> {
                                    radioValue = opt.value
                                }
                                ActionSheetSelectionMode.Tap -> {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        onSelect(opt)
                                        onDismiss()
                                    }
                                }
                            }
                        },
                    )
                }
            }

            if (selectionMode == ActionSheetSelectionMode.Radio && primaryLabel != null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val chosen = radioValue
                        if (chosen != null) {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onPrimary?.invoke(chosen)
                                onDismiss()
                            }
                        }
                    },
                    enabled = radioValue != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        disabledContainerColor = AppColors.Primary.copy(alpha = 0.35f),
                    ),
                ) {
                    Text(
                        text = primaryLabel,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font,
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            TextButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = cancelLabel,
                    color = AppColors.TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = font,
                )
            }
        }
    }
}

@Composable
private fun ActionSheetRow(
    option: ActionSheetOption,
    selected: Boolean,
    showRadio: Boolean,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val isDanger = option.tone == ActionSheetTone.Danger
    val fg = if (isDanger) AppColors.Danger else AppColors.TextPrimary
    val iconFg = if (isDanger) AppColors.Danger else AppColors.Primary
    val iconBg = if (isDanger) AppColors.DangerSoft else AppColors.PrimarySoft
    val activeBorder = if (selected) AppColors.Primary else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) AppColors.PrimarySofter else AppColors.SurfaceAlt)
            .border(1.5.dp, activeBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (option.icon != null) {
            IconBadge(
                icon = option.icon,
                foreground = iconFg,
                background = iconBg,
                size = 40.dp,
                iconSize = 18.sp,
                corner = 10.dp,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                color = fg,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            if (option.subtitle != null) {
                Text(
                    text = option.subtitle,
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    lineHeight = 16.sp,
                )
            }
        }
        if (showRadio) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (selected) AppColors.Primary else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (selected) AppColors.Primary else AppColors.Border,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    FaIcon(icon = FaIcons.CHECK, color = Color.White, size = 10.sp)
                }
            }
        }
    }
}
