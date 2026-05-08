package com.zrifapps.goservice.ui.vehicle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppTextField
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.FaIcon
import com.zrifapps.goservice.ui.theme.FaIcons
import com.zrifapps.goservice.ui.theme.FaStyle
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily

private val vehicleColors = listOf("#1A2418", "#D6453A", "#3FB1D6", "#E89C2E", "#F2EEE6", "#7B6FE8")

private fun hexToColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    Color.Gray
}

data class VehicleFormState(
    val type: String = "motor",
    val nama: String = "",
    val merek: String = "",
    val model: String = "",
    val tahun: String = "",
    val platNomor: String = "",
    val odometer: String = "",
    val warna: String = "#1A2418",
) {
    val isValid: Boolean get() = nama.isNotBlank() && merek.isNotBlank()
}

@Composable
fun VehicleForm(
    state: VehicleFormState,
    onStateChange: (VehicleFormState) -> Unit,
    modifier: Modifier = Modifier,
    showTypeSelector: Boolean = true,
) {
    val font = plusJakartaSansFontFamily()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (showTypeSelector) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                listOf("motor" to FaIcons.MOTORCYCLE, "mobil" to FaIcons.CAR).forEach { (tp, icon) ->
                    val selected = state.type == tp
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (selected) AppColors.Primary else AppColors.Surface)
                            .then(
                                if (!selected) Modifier.border(1.5.dp, AppColors.Border, RoundedCornerShape(18.dp))
                                else Modifier
                            )
                            .clickable { onStateChange(state.copy(type = tp)) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            FaIcon(
                                icon = icon,
                                color = if (selected) Color.White else AppColors.TextPrimary,
                                size = 28.sp,
                                style = FaStyle.Solid,
                            )
                            Text(
                                text = if (tp == "motor") "Motor" else "Mobil",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = font,
                                color = if (selected) Color.White else AppColors.TextPrimary,
                            )
                        }
                    }
                }
            }
        }

        AppTextField(
            label = "Nama panggilan",
            value = state.nama,
            onValueChange = { onStateChange(state.copy(nama = it)) },
            placeholder = "cth. Beat Hitam",
        )

        AppTextField(
            label = "Merk",
            value = state.merek,
            onValueChange = { onStateChange(state.copy(merek = it)) },
            placeholder = "cth. Honda",
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppTextField(
                label = "Model",
                value = state.model,
                onValueChange = { onStateChange(state.copy(model = it)) },
                placeholder = "cth. Beat",
                modifier = Modifier.weight(1f),
            )
            AppTextField(
                label = "Tahun",
                value = state.tahun,
                onValueChange = { onStateChange(state.copy(tahun = it)) },
                placeholder = "cth. 2022",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }

        AppTextField(
            label = "Plat nomor",
            value = state.platNomor,
            onValueChange = { onStateChange(state.copy(platNomor = it)) },
            placeholder = "cth. B 1234 ABC",
        )

        AppTextField(
            label = "KM saat ini",
            value = state.odometer,
            onValueChange = { onStateChange(state.copy(odometer = it)) },
            placeholder = "cth. 18000",
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        )

        Column {
            Text(
                text = "Warna",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = font,
                color = AppColors.TextMuted,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vehicleColors.forEach { hex ->
                    val selected = state.warna == hex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(hexToColor(hex))
                            .then(
                                if (selected) Modifier.border(3.dp, AppColors.Primary, RoundedCornerShape(12.dp))
                                else Modifier.border(1.dp, AppColors.Border, RoundedCornerShape(12.dp))
                            )
                            .clickable { onStateChange(state.copy(warna = hex)) },
                    )
                }
            }
        }
    }
}
