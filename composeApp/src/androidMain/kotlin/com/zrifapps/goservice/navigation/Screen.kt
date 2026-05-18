package com.zrifapps.goservice.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data object Onboarding : Screen
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
    @Serializable data object AddService : Screen
    @Serializable data object InterstitialAd : Screen
    @Serializable data object ServiceSaved : Screen
    @Serializable data object VehicleDetail : Screen
    @Serializable data object UpdateOdometer : Screen
    @Serializable data object Tips : Screen
}
