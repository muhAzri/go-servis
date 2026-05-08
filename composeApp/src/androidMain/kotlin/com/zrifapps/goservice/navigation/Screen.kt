package com.zrifapps.goservice.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable data object Splash : Screen
    @Serializable data object Onboarding : Screen
    @Serializable data object PickVehicleType : Screen
    @Serializable data class AddVehicle(val type: String) : Screen
    @Serializable data object NotifPermission : Screen
    @Serializable data object Home : Screen
    @Serializable data object AddVehicleForm : Screen
}
