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
import com.zrifapps.goservice.feature.onboarding.presentation.OnboardingVehicleInput
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.components.AppButton
import com.zrifapps.goservice.ui.components.AppInfoBanner
import com.zrifapps.goservice.ui.components.AppTextButton
import com.zrifapps.goservice.ui.onboarding.components.OnboardingStepHeader
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.vehicle.components.VehicleForm
import com.zrifapps.goservice.ui.vehicle.components.VehicleFormState

@Composable
fun OnboardingAddVehicleScreen(
    vehicleType: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onComplete: (OnboardingVehicleInput) -> Unit,
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
            onClick = { onComplete(formState.toOnboardingInput()) },
            enabled = formState.isValid && !isSubmitting,
        )

        Spacer(Modifier.height(4.dp))

        AppTextButton(text = "Lewati — tambah kendaraan nanti", onClick = onSkip)

        Spacer(Modifier.height(32.dp))
    }
}

private fun VehicleFormState.toOnboardingInput(): OnboardingVehicleInput =
    OnboardingVehicleInput(
        type = VehicleType.fromKey(type),
        nickname = nama,
        brand = merek,
        model = model,
        year = tahun.toIntOrNull(),
        plateNumber = platNomor,
        odometerKm = odometer.toLongOrNull() ?: 0L,
        colorHex = warna,
    )

@Preview(showBackground = true)
@Composable
private fun OnboardingAddVehicleScreenPreview() {
    OnboardingAddVehicleScreen(
        vehicleType = "motor",
        isSubmitting = false,
        onBack = {},
        onSkip = {},
        onComplete = {},
    )
}
