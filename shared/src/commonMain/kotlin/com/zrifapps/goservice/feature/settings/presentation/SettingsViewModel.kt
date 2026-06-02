package com.zrifapps.goservice.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    data class UiState(
        val serviceReminderNotificationsEnabled: Boolean = true,
        val odometerReminderNotificationsEnabled: Boolean = true,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observe().collect { settings ->
                _state.update {
                    it.copy(
                        serviceReminderNotificationsEnabled =
                            settings.serviceReminderNotificationsEnabled,
                        odometerReminderNotificationsEnabled =
                            settings.odometerReminderNotificationsEnabled,
                    )
                }
            }
        }
    }

    fun setServiceReminderNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setServiceReminderNotificationsEnabled(enabled)
        }
    }

    fun setOdometerReminderNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setOdometerReminderNotificationsEnabled(enabled)
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)
}
