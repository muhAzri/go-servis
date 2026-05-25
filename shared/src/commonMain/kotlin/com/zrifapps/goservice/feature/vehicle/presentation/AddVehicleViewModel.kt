package com.zrifapps.goservice.feature.vehicle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.onboarding.presentation.OnboardingVehicleInput
import com.zrifapps.goservice.feature.profile.domain.usecase.EnsureProfileSeeded
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleDraft
import com.zrifapps.goservice.feature.vehicle.domain.usecase.AddVehicle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddVehicleViewModel(
    private val addVehicle: AddVehicle,
    private val ensureProfileSeeded: EnsureProfileSeeded,
) : ViewModel() {

    data class UiState(
        val isSaving: Boolean = false,
    )

    sealed interface Event {
        data class Saved(val vehicleId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun submit(input: OnboardingVehicleInput) {
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val profileResult = ensureProfileSeeded(EnsureProfileSeeded.Params())
                val profile = when (profileResult) {
                    is DomainResult.Success -> profileResult.data
                    is DomainResult.Failure -> {
                        _events.emit(Event.Failed(profileResult.error))
                        return@launch
                    }
                }
                val draft = VehicleDraft(
                    nickname = input.nickname.trim(),
                    type = input.type,
                    subtypeId = Component.UNIVERSAL_SUBTYPE,
                    brand = input.brand.trim(),
                    model = input.model.trim(),
                    year = input.year,
                    plateNumber = input.plateNumber.trim(),
                    odometer = Distance.ofKm(input.odometerKm),
                    color = HexColor.parseOrNull(input.colorHex)
                        ?: EnsureProfileSeeded.DEFAULT_AVATAR_COLOR,
                )
                when (val r = addVehicle(AddVehicle.Params(draft = draft, ownerProfileId = profile.id))) {
                    is DomainResult.Success -> _events.emit(Event.Saved(r.data.id))
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)
}
