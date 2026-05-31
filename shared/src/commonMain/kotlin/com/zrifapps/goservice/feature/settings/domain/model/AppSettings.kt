package com.zrifapps.goservice.feature.settings.domain.model

data class AppSettings(
    val serviceReminderNotificationsEnabled: Boolean = true,
) {
    companion object {
        val default: AppSettings = AppSettings()
    }
}
