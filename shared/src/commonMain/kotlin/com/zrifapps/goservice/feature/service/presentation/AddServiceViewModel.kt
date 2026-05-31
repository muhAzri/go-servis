package com.zrifapps.goservice.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money
import com.zrifapps.goservice.feature.reminder.domain.usecase.CompleteReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecordDraft
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.service.domain.usecase.RecordService
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddServiceViewModel(
    observeVehicles: ObserveVehicles,
    private val recordService: RecordService,
    private val completeReminder: CompleteReminder,
    private val clock: AppClock,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val serviceType: ServiceType = ServiceType.OilChange,
        val serviceDateMillis: Long = 0L,
        val odometerKm: Long? = null,
        val workshop: String = "",
        val costIdr: Long = 0L,
        val note: String = "",
        val selectedComponentIds: Set<String> = emptySet(),
        val sourceReminderId: String? = null,
        val trackedComponentId: String? = null,
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val canSave: Boolean
            get() = !isLoading && !isSaving &&
                selectedVehicle != null &&
                serviceDateMillis > 0L &&
                odometerKm != null && odometerKm >= 0L
    }

    sealed interface Event {
        data class Saved(val recordId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState(serviceDateMillis = clock.nowEpochMillis()))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var preselectVehicleId: String? = null

    init {
        viewModelScope.launch {
            observeVehicles().collect { list ->
                _state.update { current ->
                    val keepId = current.selectedVehicleId?.takeIf { id -> list.any { it.id == id } }
                    val newSelectedId = keepId
                        ?: preselectVehicleId?.takeIf { id -> list.any { it.id == id } }
                        ?: list.firstOrNull()?.id
                    val newOdometer = current.odometerKm
                        ?: list.firstOrNull { it.id == newSelectedId }?.odometer?.kilometers
                    current.copy(
                        isLoading = false,
                        vehicles = list,
                        selectedVehicleId = newSelectedId,
                        odometerKm = newOdometer,
                    )
                }
            }
        }
    }

    fun preselect(
        vehicleId: String? = null,
        sourceReminderId: String? = null,
        trackedComponentId: String? = null,
    ) {
        preselectVehicleId = vehicleId
        _state.update { current ->
            val target = vehicleId?.let { id -> current.vehicles.firstOrNull { it.id == id } }
            current.copy(
                selectedVehicleId = target?.id ?: current.selectedVehicleId,
                odometerKm = target?.odometer?.kilometers ?: current.odometerKm,
                sourceReminderId = sourceReminderId ?: current.sourceReminderId,
                trackedComponentId = trackedComponentId ?: current.trackedComponentId,
            )
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

    fun submit() {
        val snapshot = _state.value
        val vehicle = snapshot.selectedVehicle ?: return
        val km = snapshot.odometerKm ?: return
        if (snapshot.isSaving || snapshot.serviceDateMillis <= 0L) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val draft = ServiceRecordDraft(
                    vehicleId = vehicle.id,
                    serviceType = snapshot.serviceType,
                    serviceDate = snapshot.serviceDateMillis,
                    odometer = Distance.ofKm(km),
                    workshop = snapshot.workshop.trim().ifBlank { null },
                    cost = Money.ofIdr(snapshot.costIdr),
                    note = snapshot.note.trim().ifBlank { null },
                    componentIds = snapshot.selectedComponentIds.toList(),
                    sourceReminderId = snapshot.sourceReminderId,
                )
                when (val r = recordService(draft)) {
                    is DomainResult.Success -> {
                        val recordId = r.data.id
                        snapshot.sourceReminderId?.let { reminderId ->
                            completeReminder(
                                CompleteReminder.Params(
                                    reminderId = reminderId,
                                    serviceRecordId = recordId,
                                ),
                            )
                        }
                        _events.emit(Event.Saved(recordId))
                    }
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
