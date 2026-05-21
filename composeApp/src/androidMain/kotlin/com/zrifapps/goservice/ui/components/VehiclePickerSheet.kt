package com.zrifapps.goservice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import kotlinx.coroutines.launch

data class VehicleOption(
    val id: String,
    val name: String,
    val plate: String,
    val icon: String,
    val accent: Color,
)

val DEFAULT_VEHICLE_OPTIONS: List<VehicleOption> = listOf(
    VehicleOption("v1", "Beat Hitam", "B 4521 KZA", FaIcons.MOTORCYCLE, AppColors.Primary),
    VehicleOption("v2", "Vario Merah", "B 6789 SKR", FaIcons.MOTORCYCLE, Color(0xFFD6453A)),
    VehicleOption("v3", "Avanza Putih", "B 1234 ABC", FaIcons.CAR, Color(0xFF3F4D5C)),
    VehicleOption("v4", "Brio Biru", "B 9876 XYZ", FaIcons.CAR, Color(0xFF3FB1D6)),
)

@Composable
fun VehiclePickerRow(
    selected: VehicleOption,
    locked: Boolean = false,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (locked) AppColors.SurfaceAlt else AppColors.Surface)
            .border(BorderStroke(1.5.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .then(if (locked) Modifier else Modifier.clickable(onClick = onClick))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = selected.icon,
            foreground = selected.accent,
            background = selected.accent.copy(alpha = 0.13f),
            size = 40.dp, iconSize = 22.sp, corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = selected.name,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = selected.plate,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
            )
        }
        FaIcon(
            icon = if (locked) FaIcons.LOCK else FaIcons.CHEVRON_DOWN,
            color = AppColors.TextSubtle,
            size = 14.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclePickerSheet(
    options: List<VehicleOption>,
    selectedId: String,
    onDismiss: () -> Unit,
    onPick: (VehicleOption) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.Surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Pilih kendaraan",
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                fontFamily = font,
            )
            Spacer(Modifier.height(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    VehicleOptionRow(
                        option = option,
                        active = option.id == selectedId,
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                onPick(option)
                                onDismiss()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleOptionRow(
    option: VehicleOption,
    active: Boolean,
    onClick: () -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) AppColors.PrimarySoft else AppColors.SurfaceAlt)
            .border(
                BorderStroke(1.5.dp, if (active) AppColors.Primary else Color.Transparent),
                RoundedCornerShape(14.dp),
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconBadge(
            icon = option.icon,
            foreground = option.accent,
            background = option.accent.copy(alpha = 0.13f),
            size = 40.dp, iconSize = 22.sp, corner = 10.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.name,
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = option.plate,
                color = AppColors.TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
            )
        }
        if (active) {
            FaIcon(icon = FaIcons.CHECK, color = AppColors.Primary, size = 16.sp)
        }
    }
}
