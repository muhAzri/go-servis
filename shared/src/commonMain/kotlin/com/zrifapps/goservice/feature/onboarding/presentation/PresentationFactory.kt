package com.zrifapps.goservice.feature.onboarding.presentation

import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import org.koin.mp.KoinPlatform

object PresentationFactory {
    fun appGateViewModel(): AppGateViewModel = KoinPlatform.getKoin().get()
    fun onboardingFlowViewModel(): OnboardingFlowViewModel = KoinPlatform.getKoin().get()

    fun vehicleInput(
        typeKey: String,
        nickname: String,
        brand: String,
        model: String,
        year: Int?,
        plateNumber: String,
        odometerKm: Long,
        colorHex: String,
    ): OnboardingVehicleInput = OnboardingVehicleInput(
        type = VehicleType.fromKey(typeKey),
        nickname = nickname,
        brand = brand,
        model = model,
        year = year,
        plateNumber = plateNumber,
        odometerKm = odometerKm,
        colorHex = colorHex,
    )
}
