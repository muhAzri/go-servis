package com.zrifapps.goservice.feature.onboarding.presentation

import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.presentation.EditProfileViewModel
import com.zrifapps.goservice.feature.profile.presentation.ProfileViewModel
import com.zrifapps.goservice.feature.reminder.presentation.ReminderListViewModel
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import org.koin.mp.KoinPlatform

object PresentationFactory {
    fun appGateViewModel(): AppGateViewModel = KoinPlatform.getKoin().get()
    fun onboardingFlowViewModel(): OnboardingFlowViewModel = KoinPlatform.getKoin().get()
    fun vehicleListViewModel(): VehicleListViewModel = KoinPlatform.getKoin().get()
    fun reminderListViewModel(): ReminderListViewModel = KoinPlatform.getKoin().get()
    fun serviceHistoryViewModel(): ServiceHistoryViewModel = KoinPlatform.getKoin().get()
    fun profileViewModel(): ProfileViewModel = KoinPlatform.getKoin().get()
    fun editProfileViewModel(): EditProfileViewModel = KoinPlatform.getKoin().get()

    fun vehicleColorHex(vehicle: Vehicle): String = vehicle.color.value
    fun profileAvatarHex(profile: Profile): String = profile.avatarColor.value
    fun formatSinceLabel(epochMillis: Long?): String =
        EditProfileViewModel.formatSinceLabel(epochMillis)

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
