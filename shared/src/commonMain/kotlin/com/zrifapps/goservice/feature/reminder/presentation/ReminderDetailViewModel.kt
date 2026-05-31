package com.zrifapps.goservice.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.CompleteReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.DeleteReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.DismissReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.ObserveReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.SnoozeReminder
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReminderDetailViewModel(
    private val observeReminder: ObserveReminder,
    private val vehicleRepository: VehicleRepository,
    private val completeReminder: CompleteReminder,
    private val snoozeReminder: SnoozeReminder,
    private val dismissReminder: DismissReminder,
    private val deleteReminder: DeleteReminder,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val isBusy: Boolean = false,
        val reminder: Reminder? = null,
        val vehicle: Vehicle? = null,
    )

    sealed interface Event {
        data object Completed : Event
        data object Snoozed : Event
        data object Dismissed : Event
        data object Deleted : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var reminderJob: Job? = null
    private var vehicleJob: Job? = null
    private var loadedId: String? = null
    private var loadedVehicleId: String? = null

    fun load(reminderId: String) {
        if (loadedId == reminderId) return
        loadedId = reminderId
        reminderJob?.cancel()
        reminderJob = viewModelScope.launch {
            observeReminder(reminderId).collect { reminder ->
                _state.update { it.copy(isLoading = false, reminder = reminder) }
                val vehicleId = reminder?.vehicleId
                if (vehicleId != null && vehicleId != loadedVehicleId) {
                    loadedVehicleId = vehicleId
                    vehicleJob?.cancel()
                    vehicleJob = viewModelScope.launch {
                        vehicleRepository.observeVehicle(vehicleId).collect { vehicle ->
                            _state.update { it.copy(vehicle = vehicle) }
                        }
                    }
                }
            }
        }
    }

    fun complete(serviceRecordId: String? = null) {
        val current = _state.value.reminder ?: return
        runAction {
            when (val r = completeReminder(
                CompleteReminder.Params(reminderId = current.id, serviceRecordId = serviceRecordId),
            )) {
                is DomainResult.Success -> _events.emit(Event.Completed)
                is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
            }
        }
    }

    fun snooze(duration: SnoozeReminder.SnoozeDuration) {
        val current = _state.value.reminder ?: return
        runAction {
            when (val r = snoozeReminder(
                SnoozeReminder.Params(reminderId = current.id, duration = duration),
            )) {
                is DomainResult.Success -> _events.emit(Event.Snoozed)
                is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
            }
        }
    }

    fun dismiss() {
        val current = _state.value.reminder ?: return
        runAction {
            when (val r = dismissReminder(current.id)) {
                is DomainResult.Success -> _events.emit(Event.Dismissed)
                is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
            }
        }
    }

    fun delete() {
        val current = _state.value.reminder ?: return
        runAction {
            when (val r = deleteReminder(current.id)) {
                is DomainResult.Success -> _events.emit(Event.Deleted)
                is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)

    private fun runAction(block: suspend () -> Unit) {
        if (_state.value.isBusy) return
        viewModelScope.launch {
            _state.update { it.copy(isBusy = true) }
            try {
                block()
            } finally {
                _state.update { it.copy(isBusy = false) }
            }
        }
    }
}
