package com.zrifapps.goservice.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.usecase.DeleteReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.ObserveReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.UpdateReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
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

class EditReminderViewModel(
    private val observeReminder: ObserveReminder,
    private val updateReminder: UpdateReminder,
    private val deleteReminder: DeleteReminder,
    private val observeVehicles: ObserveVehicles,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val reminder: Reminder? = null,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val serviceType: ServiceType = ServiceType.OilChange,
        val title: String = "",
        val triggerMode: ReminderTriggerMode = ReminderTriggerMode.Km,
        val targetKm: Long? = null,
        val targetDateMillis: Long = 0L,
        val note: String = "",
        val notifyDaysBefore: Int = 7,
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val canSave: Boolean
            get() {
                if (isLoading || isSaving) return false
                if (reminder == null || selectedVehicle == null) return false
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
        data object Saved : Event
        data object Deleted : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var reminderJob: Job? = null
    private var vehiclesJob: Job? = null
    private var loadedId: String? = null
    private var hydrated: Boolean = false

    fun load(reminderId: String) {
        if (loadedId == reminderId) return
        loadedId = reminderId
        hydrated = false
        reminderJob?.cancel()
        vehiclesJob?.cancel()

        vehiclesJob = viewModelScope.launch {
            observeVehicles().collect { list ->
                _state.update { it.copy(vehicles = list) }
            }
        }
        reminderJob = viewModelScope.launch {
            observeReminder(reminderId).collect { reminder ->
                _state.update { current ->
                    if (reminder == null) {
                        current.copy(isLoading = false, reminder = null)
                    } else if (!hydrated) {
                        hydrated = true
                        current.copy(
                            isLoading = false,
                            reminder = reminder,
                            selectedVehicleId = reminder.vehicleId,
                            serviceType = reminder.serviceType,
                            title = reminder.title,
                            triggerMode = reminder.trigger.mode,
                            targetKm = reminder.trigger.targetKm,
                            targetDateMillis = reminder.trigger.targetDateMillis ?: 0L,
                            note = reminder.note.orEmpty(),
                            notifyDaysBefore = reminder.notifyDaysBefore,
                        )
                    } else {
                        current.copy(isLoading = false, reminder = reminder)
                    }
                }
            }
        }
    }

    fun selectVehicle(vehicleId: String) {
        _state.update { current ->
            if (current.selectedVehicleId == vehicleId) return@update current
            if (current.vehicles.none { it.id == vehicleId }) return@update current
            current.copy(selectedVehicleId = vehicleId)
        }
    }

    fun setServiceType(type: ServiceType) {
        _state.update { it.copy(serviceType = type) }
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

    fun save() {
        val snapshot = _state.value
        val current = snapshot.reminder ?: return
        val vehicle = snapshot.selectedVehicle ?: return
        if (!snapshot.canSave) return
        val trigger = buildTrigger(snapshot) ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val updated = current.copy(
                    vehicleId = vehicle.id,
                    serviceType = snapshot.serviceType,
                    title = snapshot.title.trim(),
                    trigger = trigger,
                    note = snapshot.note.trim().ifBlank { null },
                    notifyDaysBefore = snapshot.notifyDaysBefore,
                )
                when (val r = updateReminder(updated)) {
                    is DomainResult.Success -> _events.emit(Event.Saved)
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun delete() {
        val current = _state.value.reminder ?: return
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                when (val r = deleteReminder(current.id)) {
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
}
