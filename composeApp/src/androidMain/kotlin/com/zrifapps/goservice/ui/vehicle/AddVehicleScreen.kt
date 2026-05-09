package com.zrifapps.goservice.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zrifapps.goservice.ui.components.AppBackButton
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.theme.plusJakartaSansFontFamily
import com.zrifapps.goservice.ui.vehicle.components.VehicleForm
import com.zrifapps.goservice.ui.vehicle.components.VehicleFormState

@Composable
fun AddVehicleScreen(
    onBack: () -> Unit,
    onSaved: (VehicleFormState) -> Unit,
) {
    val font = plusJakartaSansFontFamily()
    var formState by remember { mutableStateOf(VehicleFormState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppBackButton(onClick = onBack)
            Text(
                text = "Tambah Kendaraan",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = font,
                color = AppColors.TextPrimary,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 20.dp),
        ) {
            VehicleForm(
                state = formState,
                onStateChange = { formState = it },
                showTypeSelector = true,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.Surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Border)
                    .padding(bottom = 1.dp),
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).padding(bottom = 14.dp),
            ) {
                AppButton(
                    text = "Simpan Kendaraan",
                    onClick = { onSaved(formState) },
                    enabled = formState.isValid,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddVehicleScreenPreview() {
    AddVehicleScreen(onBack = {}, onSaved = {})
}
