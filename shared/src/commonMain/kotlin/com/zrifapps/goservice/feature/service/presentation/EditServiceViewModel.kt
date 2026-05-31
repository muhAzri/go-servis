package com.zrifapps.goservice.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.service.domain.usecase.DeleteServiceRecord
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveServiceRecord
import com.zrifapps.goservice.feature.service.domain.usecase.UpdateService
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditServiceViewModel(
    private val observeServiceRecord: ObserveServiceRecord,
    private val updateService: UpdateService,
    private val deleteServiceRecord: DeleteServiceRecord,
    private val observeVehicles: ObserveVehicles,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val record: ServiceRecord? = null,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val serviceType: ServiceType = ServiceType.OilChange,
        val serviceDateMillis: Long = 0L,
        val odometerKm: Long? = null,
        val workshop: String = "",
        val costIdr: Long = 0L,
        val note: String = "",
        val selectedComponentIds: Set<String> = emptySet(),
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val canSave: Boolean
            get() = !isLoading && !isSaving && record != null &&
                selectedVehicle != null &&
                serviceDateMillis > 0L &&
                odometerKm != null && odometerKm >= 0L
    }

    sealed interface Event {
        data object Saved : Event
        data object Deleted : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var recordJob: Job? = null
    private var vehiclesJob: Job? = null
    private var loadedId: String? = null
    private var hydrated: Boolean = false

    fun load(recordId: String) {
        if (loadedId == recordId) return
        loadedId = recordId
        hydrated = false
        recordJob?.cancel()
        vehiclesJob?.cancel()

        vehiclesJob = viewModelScope.launch {
            observeVehicles().collect { list ->
                _state.update { it.copy(vehicles = list) }
            }
        }
        recordJob = viewModelScope.launch {
            observeServiceRecord(recordId).collect { record ->
                _state.update { current ->
                    if (record == null) {
                        current.copy(isLoading = false, record = null)
                    } else if (!hydrated) {
                        hydrated = true
                        current.copy(
                            isLoading = false,
                            record = record,
                            selectedVehicleId = record.vehicleId,
                            serviceType = record.serviceType,
                            serviceDateMillis = record.serviceDate,
                            odometerKm = record.odometer.kilometers,
                            workshop = record.workshop.orEmpty(),
                            costIdr = record.cost.amountIdr,
                            note = record.note.orEmpty(),
                            selectedComponentIds = record.componentIds.toSet(),
                        )
                    } else {
                        current.copy(isLoading = false, record = record)
                    }
                }
            }
        }
    }

    fun selectVehicle(vehicleId: String) {
        _state.update { current ->
            if (current.selectedVehicleId == vehicleId) return@update current
            val target = current.vehicles.firstOrNull { it.id == vehicleId } ?: return@update current
            current.copy(
                selectedVehicleId = vehicleId,
                odometerKm = target.odometer.kilometers,
            )
        }
    }

    fun setServiceType(type: ServiceType) {
        _state.update { it.copy(serviceType = type) }
    }

    fun setServiceDate(millis: Long) {
        _state.update { it.copy(serviceDateMillis = millis) }
    }

    fun setOdometer(km: Long?) {
        _state.update { it.copy(odometerKm = km?.coerceAtLeast(0L)) }
    }

    fun setWorkshop(value: String) {
        _state.update { it.copy(workshop = value) }
    }

    fun setCost(amountIdr: Long) {
        _state.update { it.copy(costIdr = amountIdr.coerceAtLeast(0L)) }
    }

    fun setNote(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun toggleComponent(componentId: String) {
        _state.update { current ->
            val next = current.selectedComponentIds.toMutableSet().apply {
                if (!add(componentId)) remove(componentId)
            }
            current.copy(selectedComponentIds = next)
        }
    }

    fun setComponents(ids: Set<String>) {
        _state.update { it.copy(selectedComponentIds = ids) }
    }

    fun save() {
        val snapshot = _state.value
        val current = snapshot.record ?: return
        val vehicle = snapshot.selectedVehicle ?: return
        val km = snapshot.odometerKm ?: return
        if (snapshot.isSaving || snapshot.serviceDateMillis <= 0L) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val updated = current.copy(
                    vehicleId = vehicle.id,
                    serviceType = snapshot.serviceType,
                    serviceDate = snapshot.serviceDateMillis,
                    odometer = Distance.ofKm(km),
                    workshop = snapshot.workshop.trim().ifBlank { null },
                    cost = Money.ofIdr(snapshot.costIdr),
                    note = snapshot.note.trim().ifBlank { null },
                    componentIds = snapshot.selectedComponentIds.toList(),
                )
                when (val r = updateService(updated)) {
                    is DomainResult.Success -> _events.emit(Event.Saved)
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun delete() {
        val current = _state.value.record ?: return
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                when (val r = deleteServiceRecord(current.id)) {
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
}
