package com.zrifapps.goservice.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.profile.domain.model.Profile
import com.zrifapps.goservice.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    profileRepository: ProfileRepository,
) : ViewModel() {

    data class UiState(
        val profile: Profile? = null,
        val isLoading: Boolean = true,
    ) {
        val displayName: String get() = profile?.name?.takeIf(String::isNotBlank) ?: "Kamu"
        val avatarColorHex: String? get() = profile?.avatarColor?.value
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.observeCurrent().collect { profile ->
                _state.update { it.copy(profile = profile, isLoading = false) }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)
}
