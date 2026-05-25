package com.zrifapps.goservice.feature.vehicle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import com.zrifapps.goservice.feature.vehicle.domain.usecase.UpdateOdometer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpdateOdometerViewModel(
    observeVehicles: ObserveVehicles,
    private val updateOdometer: UpdateOdometer,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedId: String? = null,
        val odometerKm: Long? = null,
    ) {
        val selected: Vehicle? get() = vehicles.firstOrNull { it.id == selectedId }
        val canSave: Boolean
            get() = !isLoading && !isSaving && selected != null &&
                odometerKm != null && odometerKm >= 0L
    }

    sealed interface Event {
        data class Saved(val vehicleId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var preselectId: String? = null

    init {
        viewModelScope.launch {
            observeVehicles().collect { list ->
                _state.update { current ->
                    val keepId = current.selectedId?.takeIf { id -> list.any { it.id == id } }
                    val newSelectedId = keepId
                        ?: preselectId?.takeIf { id -> list.any { it.id == id } }
                        ?: list.firstOrNull()?.id
                    val newOdometer = current.odometerKm
                        ?: list.firstOrNull { it.id == newSelectedId }?.odometer?.kilometers
                    current.copy(
                        isLoading = false,
                        vehicles = list,
                        selectedId = newSelectedId,
                        odometerKm = newOdometer,
                    )
                }
            }
        }
    }

    fun preselect(vehicleId: String?) {
        preselectId = vehicleId
        if (vehicleId == null) return
        _state.update { current ->
            if (current.vehicles.none { it.id == vehicleId }) return@update current
            val target = current.vehicles.firstOrNull { it.id == vehicleId } ?: return@update current
            current.copy(
                selectedId = vehicleId,
                odometerKm = target.odometer.kilometers,
            )
        }
    }

    fun select(vehicleId: String) {
        _state.update { current ->
            if (current.selectedId == vehicleId) return@update current
            val target = current.vehicles.firstOrNull { it.id == vehicleId } ?: return@update current
            current.copy(
                selectedId = vehicleId,
                odometerKm = target.odometer.kilometers,
            )
        }
    }

    fun setOdometer(km: Long?) {
        _state.update { it.copy(odometerKm = km?.coerceAtLeast(0L)) }
    }

    fun save(allowRollback: Boolean = false) {
        val snapshot = _state.value
        val target = snapshot.selected ?: return
        val km = snapshot.odometerKm ?: return
        if (snapshot.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val result = updateOdometer(
                    UpdateOdometer.Params(
                        vehicleId = target.id,
                        odometer = Distance.ofKm(km),
                        allowRollback = allowRollback,
                    ),
                )
                when (result) {
                    is DomainResult.Success -> _events.emit(Event.Saved(result.data.id))
                    is DomainResult.Failure -> _events.emit(Event.Failed(result.error))
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
