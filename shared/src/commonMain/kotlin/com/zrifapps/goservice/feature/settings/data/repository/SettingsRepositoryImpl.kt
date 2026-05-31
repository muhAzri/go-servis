package com.zrifapps.goservice.feature.settings.data.repository

import com.zrifapps.goservice.feature.settings.data.local.SettingsStore
import com.zrifapps.goservice.feature.settings.domain.model.AppSettings
import com.zrifapps.goservice.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(
    private val store: SettingsStore,
) : SettingsRepository {

    private val _settings = MutableStateFlow(load())

    override fun observe(): Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun setServiceReminderNotificationsEnabled(enabled: Boolean) {
        store.putBoolean(KEY_SERVICE_REMINDER_NOTIFICATIONS, enabled)
        _settings.update { it.copy(serviceReminderNotificationsEnabled = enabled) }
    }

    private fun load(): AppSettings = AppSettings(
        serviceReminderNotificationsEnabled =
            store.getBoolean(KEY_SERVICE_REMINDER_NOTIFICATIONS, AppSettings.default.serviceReminderNotificationsEnabled),
    )

    private companion object {
        const val KEY_SERVICE_REMINDER_NOTIFICATIONS = "service_reminder_notifications_enabled"
    }
}
