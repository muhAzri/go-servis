package com.zrifapps.goservice.feature.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.onboarding.domain.usecase.ObserveOnboarding
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppGateViewModel(
    observeOnboarding: ObserveOnboarding,
) : ViewModel() {

    val gate: StateFlow<AppGate> = observeOnboarding()
        .map { state ->
            if (state.completed) AppGate.Main
            else AppGate.Onboarding(resumeStep = state.currentStep)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = AppGate.Loading,
        )

    fun observeGate(onChange: (AppGate) -> Unit): Cancellable =
        gate.subscribeOn(viewModelScope, onChange)

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
