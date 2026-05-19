package com.zrifapps.goservice.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.AppInfoBanner
import com.zrifapps.goservice.ui.onboarding.components.OnboardingStepHeader
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.vehicle.components.VehicleForm
import com.zrifapps.goservice.ui.vehicle.components.VehicleFormState

@Composable
fun OnboardingAddVehicleScreen(
    vehicleType: String,
    onBack: () -> Unit,
    onComplete: (VehicleFormState) -> Unit,
) {
    var formState by remember { mutableStateOf(VehicleFormState(type = vehicleType)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BgWarm)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(8.dp))

        OnboardingStepHeader(
            stepLabel = "Langkah 3 dari 4",
            title = if (vehicleType == "motor") "Detail motormu" else "Detail mobilmu",
            subtitle = "Isi data kendaraan supaya pengingat servis lebih akurat.",
            onBack = onBack,
        )

        Spacer(Modifier.height(28.dp))

        VehicleForm(
            state = formState,
            onStateChange = { formState = it },
            showTypeSelector = false,
        )

        Spacer(Modifier.height(24.dp))

        AppInfoBanner("Kamu bisa ubah data ini kapan saja nanti.")

        Spacer(Modifier.height(16.dp))

        AppButton(
            text = "Lanjut",
            onClick = { onComplete(formState) },
            enabled = formState.isValid,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingAddVehicleScreenPreview() {
    OnboardingAddVehicleScreen(vehicleType = "motor", onBack = {}, onComplete = {})
}
