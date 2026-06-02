package com.zrifapps.goservice.feature.settings.domain.repository

import com.zrifapps.goservice.feature.settings.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun observe(): Flow<AppSettings>

    suspend fun setServiceReminderNotificationsEnabled(enabled: Boolean)

    suspend fun setOdometerReminderNotificationsEnabled(enabled: Boolean)
}
