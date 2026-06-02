package com.zrifapps.goservice.feature.vehicle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.feature.onboarding.presentation.OnboardingVehicleInput
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import com.zrifapps.goservice.feature.vehicle.domain.usecase.DeleteVehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import com.zrifapps.goservice.feature.vehicle.domain.usecase.UpdateVehicle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditVehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val updateVehicle: UpdateVehicle,
    private val deleteVehicle: DeleteVehicle,
    private val observeVehicles: ObserveVehicles,
    private val clock: AppClock,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicle: Vehicle? = null,
    )

    sealed interface Event {
        data object Saved : Event
        data object Deleted : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var observeJob: Job? = null
    private var loadedId: String? = null

    fun load(vehicleId: String?) {
        if (vehicleId != null) {
            if (loadedId == vehicleId) return
            loadedId = vehicleId
            observeJob?.cancel()
            observeJob = viewModelScope.launch {
                vehicleRepository.observeVehicle(vehicleId).collect { vehicle ->
                    _state.update { it.copy(isLoading = false, vehicle = vehicle) }
                }
            }
        } else {
            if (loadedId == FALLBACK_KEY) return
            loadedId = FALLBACK_KEY
            observeJob?.cancel()
            observeJob = viewModelScope.launch {
                observeVehicles().collect { list ->
                    val pick = list.firstOrNull()
                    _state.update { it.copy(isLoading = false, vehicle = pick) }
                }
            }
        }
    }

    fun save(input: OnboardingVehicleInput) {
        val current = _state.value.vehicle ?: return
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val newOdometer = Distance.ofKm(input.odometerKm)
                val odometerChanged = newOdometer != current.odometer
                val updated = current.copy(
                    nickname = input.nickname.trim(),
                    type = input.type,
                    subtypeId = input.subtypeId?.takeIf(String::isNotBlank) ?: current.subtypeId,
                    brand = input.brand.trim(),
                    model = input.model.trim(),
                    year = input.year,
                    plateNumber = input.plateNumber.trim(),
                    odometer = newOdometer,
                    color = HexColor.parseOrNull(input.colorHex) ?: current.color,
                    lastOdometerUpdateAt = if (odometerChanged) clock.nowEpochMillis() else current.lastOdometerUpdateAt,
                )
                when (val r = updateVehicle(updated)) {
                    is DomainResult.Success -> _events.emit(Event.Saved)
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun delete() {
        val current = _state.value.vehicle ?: return
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                when (val r = deleteVehicle(current.id)) {
                    is DomainResult.Success -> _events.emit(Event.Deleted)
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

    private companion object {
        const val FALLBACK_KEY = "__first__"
    }
}
