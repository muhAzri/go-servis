package com.zrifapps.goservice.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
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

class AddReminderViewModel(
    observeVehicles: ObserveVehicles,
    private val addReminder: AddReminder,
    clock: AppClock,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val serviceType: ServiceType = ServiceType.OilChange,
        val title: String = "",
        val triggerMode: ReminderTriggerMode = ReminderTriggerMode.Km,
        val targetKm: Long? = null,
        val targetDateMillis: Long = 0L,
        val note: String = "",
        val notifyDaysBefore: Int = 7,
        val trackedComponentId: String? = null,
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val canSave: Boolean
            get() {
                if (isLoading || isSaving) return false
                if (selectedVehicle == null) return false
                if (title.isBlank()) return false
                if (notifyDaysBefore < 0) return false
                return when (triggerMode) {
                    ReminderTriggerMode.Km -> (targetKm ?: 0L) > 0L
                    ReminderTriggerMode.Date -> targetDateMillis > 0L
                    ReminderTriggerMode.Both -> (targetKm ?: 0L) > 0L && targetDateMillis > 0L
                }
            }
    }

    sealed interface Event {
        data class Saved(val reminderId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(
        UiState(
            targetDateMillis = clock.nowEpochMillis(),
            title = deriveTitle(ServiceType.OilChange),
        ),
    )
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
                    val baseKm = list.firstOrNull { it.id == newSelectedId }?.odometer?.kilometers
                    val nextTargetKm = current.targetKm
                        ?: baseKm?.let { it + DEFAULT_KM_INCREMENT }
                    current.copy(
                        isLoading = false,
                        vehicles = list,
                        selectedVehicleId = newSelectedId,
                        targetKm = nextTargetKm,
                    )
                }
            }
        }
    }

    fun preselect(
        vehicleId: String? = null,
        trackedComponentId: String? = null,
    ) {
        preselectVehicleId = vehicleId
        _state.update { current ->
            val target = vehicleId?.let { id -> current.vehicles.firstOrNull { it.id == id } }
            current.copy(
                selectedVehicleId = target?.id ?: current.selectedVehicleId,
                trackedComponentId = trackedComponentId ?: current.trackedComponentId,
            )
        }
    }

    fun selectVehicle(vehicleId: String) {
        _state.update { current ->
            if (current.selectedVehicleId == vehicleId) return@update current
            val target = current.vehicles.firstOrNull { it.id == vehicleId } ?: return@update current
            val newTargetKm = current.targetKm
                ?: (target.odometer.kilometers + DEFAULT_KM_INCREMENT)
            current.copy(selectedVehicleId = vehicleId, targetKm = newTargetKm)
        }
    }

    fun setServiceType(type: ServiceType) {
        _state.update { current ->
            val nextTitle = if (current.title == deriveTitle(current.serviceType) || current.title.isBlank()) {
                deriveTitle(type)
            } else {
                current.title
            }
            current.copy(serviceType = type, title = nextTitle)
        }
    }

    fun setTitle(value: String) {
        _state.update { it.copy(title = value) }
    }

    fun setTriggerMode(mode: ReminderTriggerMode) {
        _state.update { it.copy(triggerMode = mode) }
    }

    fun setTargetKm(km: Long?) {
        _state.update { it.copy(targetKm = km?.coerceAtLeast(0L)) }
    }

    fun setTargetDate(millis: Long) {
        _state.update { it.copy(targetDateMillis = millis) }
    }

    fun setNote(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun setNotifyDaysBefore(days: Int) {
        _state.update { it.copy(notifyDaysBefore = days.coerceAtLeast(0)) }
    }

    fun submit() {
        val snapshot = _state.value
        if (!snapshot.canSave) return
        val vehicle = snapshot.selectedVehicle ?: return
        val trigger = buildTrigger(snapshot) ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val draft = ReminderDraft(
                    vehicleId = vehicle.id,
                    serviceType = snapshot.serviceType,
                    trackedComponentId = snapshot.trackedComponentId,
                    title = snapshot.title.trim(),
                    trigger = trigger,
                    note = snapshot.note.trim().ifBlank { null },
                    notifyDaysBefore = snapshot.notifyDaysBefore,
                )
                when (val r = addReminder(draft)) {
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

    private fun buildTrigger(s: UiState): ReminderTrigger? = when (s.triggerMode) {
        ReminderTriggerMode.Km -> s.targetKm?.let { ReminderTrigger.ByKm(Distance.ofKm(it)) }
        ReminderTriggerMode.Date -> if (s.targetDateMillis > 0L) ReminderTrigger.ByDate(s.targetDateMillis) else null
        ReminderTriggerMode.Both -> {
            val km = s.targetKm
            if (km != null && s.targetDateMillis > 0L) {
                ReminderTrigger.ByBoth(Distance.ofKm(km), s.targetDateMillis)
            } else null
        }
    }

    private companion object {
        const val DEFAULT_KM_INCREMENT = 2_000L

        fun deriveTitle(type: ServiceType): String = when (type) {
            ServiceType.OilChange -> "Ganti Oli"
            ServiceType.Filter -> "Ganti Filter"
            ServiceType.Tire -> "Rotasi/Ganti Ban"
            ServiceType.Battery -> "Cek Aki"
            ServiceType.Brake -> "Ganti Kampas Rem"
            ServiceType.Radiator -> "Cek Radiator"
            ServiceType.TuneUp -> "Tune-up"
            ServiceType.Other -> "Pengingat servis"
        }
    }
}
