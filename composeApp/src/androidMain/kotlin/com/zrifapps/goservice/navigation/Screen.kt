package com.zrifapps.goservice.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data object Onboarding : Screen
    @Serializable data object Name : Screen
    @Serializable data object PickVehicleType : Screen
    @Serializable data class AddVehicle(val type: String) : Screen
    @Serializable data object NotifPermission : Screen
    @Serializable data object Main : Screen
    @Serializable data object AddVehicleForm : Screen
    @Serializable data object Test : Screen
    @Serializable data object Privacy : Screen
    @Serializable data object Terms : Screen
    @Serializable data object About : Screen
    @Serializable data object Help : Screen

    // Core flow
    @Serializable data object ReminderDetail : Screen
    @Serializable data object AddReminder : Screen
    @Serializable data class AddReminderFromContext(val fromContext: Boolean = true) : Screen
    @Serializable data class AddService(
        val vehicleId: String? = null,
        val sourceReminderId: String? = null,
        val trackedComponentId: String? = null,
    ) : Screen
    @Serializable data class EditService(val recordId: String) : Screen
    @Serializable data class InterstitialAd(val recordId: String? = null) : Screen
    @Serializable data class ServiceSaved(val recordId: String? = null) : Screen
    @Serializable data class ServiceDetail(val recordId: String) : Screen
    @Serializable data class VehicleDetail(val vehicleId: String? = null) : Screen
    @Serializable data class UpdateOdometer(val vehicleId: String? = null) : Screen
    @Serializable data object Tips : Screen
    @Serializable data object TipsDetail : Screen
    @Serializable data object EditProfile : Screen

    // Component management
    @Serializable data class VehicleComponents(val vehicleId: String) : Screen
    @Serializable data class ComponentInfo(
        val vehicleId: String,
        val catalogId: String? = null,
        val customName: String? = null,
    ) : Screen
    @Serializable data class TrackedComponentDetail(val trackedId: String) : Screen
    @Serializable data class AddCustomComponent(val vehicleId: String) : Screen

    // New screens from plot-hole design
    @Serializable data class EditVehicle(val vehicleId: String? = null) : Screen
    @Serializable data object EditReminder : Screen
    @Serializable data object VehicleList : Screen
}
