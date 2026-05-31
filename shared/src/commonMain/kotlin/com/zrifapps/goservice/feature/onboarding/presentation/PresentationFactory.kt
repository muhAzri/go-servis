package com.zrifapps.goservice.feature.onboarding.presentation

import com.zrifapps.goservice.feature.component.presentation.AddTrackedComponentViewModel
import com.zrifapps.goservice.feature.component.presentation.ComponentInfoViewModel
import com.zrifapps.goservice.feature.component.presentation.TrackedComponentDetailViewModel
import com.zrifapps.goservice.feature.backup.presentation.BackupViewModel
import com.zrifapps.goservice.feature.component.presentation.VehicleComponentsViewModel
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.presentation.EditProfileViewModel
import com.zrifapps.goservice.feature.profile.presentation.ProfileViewModel
import com.zrifapps.goservice.feature.reminder.presentation.AddReminderViewModel
import com.zrifapps.goservice.feature.reminder.presentation.EditReminderViewModel
import com.zrifapps.goservice.feature.reminder.presentation.ReminderDetailViewModel
import com.zrifapps.goservice.feature.reminder.presentation.ReminderListViewModel
import com.zrifapps.goservice.feature.service.presentation.AddServiceViewModel
import com.zrifapps.goservice.feature.service.presentation.EditServiceViewModel
import com.zrifapps.goservice.feature.service.presentation.ServiceDetailViewModel
import com.zrifapps.goservice.feature.service.presentation.ServiceHistoryViewModel
import com.zrifapps.goservice.feature.settings.presentation.SettingsViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.presentation.AddVehicleViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.EditVehicleViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.UpdateOdometerViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleDetailViewModel
import com.zrifapps.goservice.feature.vehicle.presentation.VehicleListViewModel
import org.koin.mp.KoinPlatform

object PresentationFactory {
    fun appGateViewModel(): AppGateViewModel = KoinPlatform.getKoin().get()
    fun onboardingFlowViewModel(): OnboardingFlowViewModel = KoinPlatform.getKoin().get()
    fun vehicleListViewModel(): VehicleListViewModel = KoinPlatform.getKoin().get()
    fun addVehicleViewModel(): AddVehicleViewModel = KoinPlatform.getKoin().get()
    fun editVehicleViewModel(): EditVehicleViewModel = KoinPlatform.getKoin().get()
    fun updateOdometerViewModel(): UpdateOdometerViewModel = KoinPlatform.getKoin().get()
    fun vehicleDetailViewModel(): VehicleDetailViewModel = KoinPlatform.getKoin().get()
    fun reminderListViewModel(): ReminderListViewModel = KoinPlatform.getKoin().get()
    fun addReminderViewModel(): AddReminderViewModel = KoinPlatform.getKoin().get()
    fun editReminderViewModel(): EditReminderViewModel = KoinPlatform.getKoin().get()
    fun reminderDetailViewModel(): ReminderDetailViewModel = KoinPlatform.getKoin().get()
    fun serviceHistoryViewModel(): ServiceHistoryViewModel = KoinPlatform.getKoin().get()
    fun addServiceViewModel(): AddServiceViewModel = KoinPlatform.getKoin().get()
    fun editServiceViewModel(): EditServiceViewModel = KoinPlatform.getKoin().get()
    fun serviceDetailViewModel(): ServiceDetailViewModel = KoinPlatform.getKoin().get()
    fun profileViewModel(): ProfileViewModel = KoinPlatform.getKoin().get()
    fun editProfileViewModel(): EditProfileViewModel = KoinPlatform.getKoin().get()
    fun vehicleComponentsViewModel(): VehicleComponentsViewModel = KoinPlatform.getKoin().get()
    fun trackedComponentDetailViewModel(): TrackedComponentDetailViewModel = KoinPlatform.getKoin().get()
    fun addTrackedComponentViewModel(): AddTrackedComponentViewModel = KoinPlatform.getKoin().get()
    fun componentInfoViewModel(): ComponentInfoViewModel = KoinPlatform.getKoin().get()
    fun settingsViewModel(): SettingsViewModel = KoinPlatform.getKoin().get()
    fun backupViewModel(): BackupViewModel = KoinPlatform.getKoin().get()

    fun vehicleColorHex(vehicle: Vehicle): String = vehicle.color.value
    fun profileAvatarHex(profile: Profile): String = profile.avatarColor.value
    fun formatSinceLabel(epochMillis: Long?): String =
        EditProfileViewModel.formatSinceLabel(epochMillis)

    fun vehicleShareText(vehicle: Vehicle): String {
        val title = vehicle.displayTitle
        val brandModel = listOf(vehicle.brand, vehicle.model)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .joinToString(" ")
        val brandLine = listOfNotNull(
            brandModel.takeIf(String::isNotEmpty),
            vehicle.year?.toString(),
        ).joinToString(" · ")
        val plate = vehicle.plateNumber.trim().takeIf(String::isNotEmpty)
        val km = formatKmGrouped(vehicle.odometer.kilometers)
        return buildString {
            appendLine("🛵 $title")
            if (brandLine.isNotEmpty()) appendLine(brandLine)
            if (plate != null) appendLine("Plat: $plate")
            appendLine("KM saat ini: $km km")
            appendLine()
            appendLine("—")
            appendLine("Dicatat pakai GoService.")
            append("Pengingat servis & catatan kendaraan, biar gak telat lagi.")
        }
    }

    private fun formatKmGrouped(km: Long): String {
        if (km == 0L) return "0"
        val abs = kotlin.math.abs(km).toString()
        val grouped = abs.reversed().chunked(3).joinToString(".").reversed()
        return if (km < 0) "-$grouped" else grouped
    }

    fun vehicleInput(
        typeKey: String,
        nickname: String,
        brand: String,
        model: String,
        year: Int?,
        plateNumber: String,
        odometerKm: Long,
        colorHex: String,
        subtypeId: String? = null,
    ): OnboardingVehicleInput = OnboardingVehicleInput(
        type = VehicleType.fromKey(typeKey),
        nickname = nickname,
        brand = brand,
        model = model,
        year = year,
        plateNumber = plateNumber,
        odometerKm = odometerKm,
        colorHex = colorHex,
        subtypeId = subtypeId,
    )
}
