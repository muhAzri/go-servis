package com.zrifapps.goservice.ui.vehicle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.UpdateOdometerViewModel
import com.zrifapps.goservice.ui.common.toastError
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.CircleIconButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import org.koin.androidx.compose.koinViewModel

@Composable
fun UpdateOdometerScreen(
    vehicleId: String? = null,
    onClose: () -> Unit,
    onSaved: () -> Unit = {},
    vm: UpdateOdometerViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current

    LaunchedEffect(vehicleId) { vm.preselect(vehicleId) }
    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is UpdateOdometerViewModel.Event.Saved -> onSaved()
                is UpdateOdometerViewModel.Event.Failed -> ctx.toastError(event.error)
            }
        }
    }

    val selected = state.selected
    val font = plusJakartaSansFontFamily()
    val odometerText = state.odometerKm?.toString().orEmpty()
    val lastKm = selected?.odometer?.kilometers ?: 0L
    val displayName = selected?.displayTitle ?: ""
    val displayPlate = selected?.plateNumber ?: ""

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
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            if (state.vehicles.size > 1) {
                Text(
                    text = "PILIH KENDARAAN",
                    color = AppColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    fontFamily = font,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.vehicles.forEach { v ->
                        VehicleChip(
                            vehicle = v,
                            active = v.id == state.selectedId,
                            onClick = { vm.select(v.id) },
                            font = font,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            } else if (selected != null) {
                Text(
                    text = "$displayName · $displayPlate",
                    color = AppColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font,
                    modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
                )
            }

            Text(
                text = "KM terakhir tercatat: ${formatOdometerLong(lastKm)} km",
                color = AppColors.TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            OdometerDisplay(value = odometerText, lastKm = lastKm)
            Spacer(Modifier.height(20.dp))
            NumericKeypad(
                onKey = { key ->
                    val next = applyKey(odometerText, key)
                    vm.setOdometer(next.toLongOrNull())
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface)
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp)),
        ) {
            AppButton(
                text = "Simpan KM",
                onClick = { vm.save() },
                enabled = state.canSave,
            )
        }
    }
}

@Composable
private fun VehicleChip(
    vehicle: Vehicle,
    active: Boolean,
    onClick: () -> Unit,
    font: FontFamily,
) {
    val bg = if (active) AppColors.Primary else AppColors.Surface
    val borderColor = if (active) AppColors.Primary else AppColors.Border
    val textColor = if (active) Color.White else AppColors.TextPrimary
    val plateColor = if (active) Color.White.copy(alpha = 0.8f) else AppColors.TextMuted
    val accent = parseHexColorOrDefault(vehicle.color.value)
    val icon = if (vehicle.type == VehicleType.Mobil) FaIcons.CAR else FaIcons.MOTORCYCLE

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    if (active) Color.White.copy(alpha = 0.22f)
                    else accent.copy(alpha = 0.14f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            FaIcon(
                icon = icon,
                color = if (active) Color.White else accent,
                size = 14.sp,
            )
        }
        Column {
            Text(
                text = vehicle.displayTitle,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = font,
            )
            Text(
                text = vehicle.plateNumber,
                color = plateColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
            )
        }
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
            text = "Update KM",
            color = AppColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = font,
        )
    }
}

@Composable
private fun OdometerDisplay(value: String, lastKm: Long) {
    val font = plusJakartaSansFontFamily()
    val formatted = remember(value) { if (value.isEmpty()) "0" else formatOdometer(value) }
    val delta = remember(value, lastKm) { (value.toLongOrNull() ?: 0L) - lastKm }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(2.dp, AppColors.Primary), RoundedCornerShape(22.dp))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "KM SAAT INI",
            color = AppColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = font,
        )
        Text(
            text = formatted,
            color = AppColors.TextPrimary,
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (delta >= 0) "+${formatOdometerLong(delta)} km dari terakhir"
            else "${formatOdometerLong(delta)} km dari terakhir",
            color = AppColors.Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = font,
        )
    }
}

@Composable
private fun NumericKeypad(onKey: (String) -> Unit) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "⌫")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { k ->
                    KeyTile(label = k, onClick = { onKey(k) }, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun KeyTile(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.Surface)
            .border(BorderStroke(1.dp, AppColors.Border), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = AppColors.TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

private fun formatOdometer(raw: String): String {
    val n = raw.toLongOrNull() ?: return raw
    return formatOdometerLong(n)
}

private fun formatOdometerLong(n: Long): String {
    val sign = if (n < 0) "-" else ""
    val abs = kotlin.math.abs(n).toString()
    return sign + abs.reversed().chunked(3).joinToString(".").reversed()
}

private fun applyKey(current: String, key: String): String = when (key) {
    "⌫" -> if (current.isEmpty()) current else current.dropLast(1)
    "." -> if (current.contains(".")) current else "$current."
    else -> current + key
}

private fun parseHexColorOrDefault(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: IllegalArgumentException) {
    AppColors.Primary
}
