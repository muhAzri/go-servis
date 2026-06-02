package com.zrifapps.goservice.feature.reminder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalogAll
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddReminderViewModel(
    observeVehicles: ObserveVehicles,
    private val addReminder: AddReminder,
    private val observeTrackedComponents: ObserveTrackedComponents,
    private val observeComponentCatalogAll: ObserveComponentCatalogAll,
    private val clock: AppClock,
) : ViewModel() {

    enum class Mode(val key: String) {
        Komponen("komponen"),
        Manual("manual");

        companion object {
            fun fromKey(key: String?): Mode = entries.firstOrNull { it.key == key } ?: Komponen
        }
    }

    data class ComponentOption(
        val trackedId: String,
        val catalogId: String,
        val label: String,
        val iconKey: String,
        val colorHex: String,
        val intervalKm: Long?,
        val intervalDays: Int?,
        val intervalLabel: String,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val mode: Mode = Mode.Komponen,
        val availableComponents: List<ComponentOption> = emptyList(),
        val isLoadingComponents: Boolean = false,
        val selectedComponentTrackedId: String? = null,
        val title: String = "",
        val triggerMode: ReminderTriggerMode = ReminderTriggerMode.Both,
        val targetKm: Long? = null,
        val targetDateMillis: Long = 0L,
        val note: String = "",
        val notifyDaysBefore: Int = 7,
        val preselectedTrackedComponentId: String? = null,
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val hasTrackedComponents: Boolean get() = availableComponents.isNotEmpty()
        val selectedComponent: ComponentOption?
            get() = availableComponents.firstOrNull { it.trackedId == selectedComponentTrackedId }

        val canSave: Boolean
            get() {
                if (isLoading || isSaving) return false
                if (selectedVehicle == null) return false
                if (notifyDaysBefore < 0) return false
                return when (mode) {
                    Mode.Komponen -> selectedComponent != null &&
                        ((targetKm ?: 0L) > 0L || targetDateMillis > 0L)
                    Mode.Manual -> {
                        if (title.isBlank()) return false
                        when (triggerMode) {
                            ReminderTriggerMode.Km -> (targetKm ?: 0L) > 0L
                            ReminderTriggerMode.Date -> targetDateMillis > 0L
                            ReminderTriggerMode.Both -> (targetKm ?: 0L) > 0L && targetDateMillis > 0L
                        }
                    }
                }
            }
    }

    sealed interface Event {
        data class Saved(val reminderId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(
        UiState(targetDateMillis = clock.nowEpochMillis()),
    )
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var preselectVehicleId: String? = null
    private var componentsJob: Job? = null
    private var observingVehicleId: String? = null

    init {
        viewModelScope.launch {
            observeVehicles().collect { list ->
                _state.update { current ->
                    val keepId = current.selectedVehicleId?.takeIf { id -> list.any { it.id == id } }
                    val newSelectedId = keepId
                        ?: preselectVehicleId?.takeIf { id -> list.any { it.id == id } }
                        ?: list.firstOrNull()?.id
                    current.copy(
                        isLoading = false,
                        vehicles = list,
                        selectedVehicleId = newSelectedId,
                    )
                }
                refreshComponentsObservation(_state.value.selectedVehicleId)
            }
        }
    }

    private fun refreshComponentsObservation(vehicleId: String?) {
        if (vehicleId == observingVehicleId) return
        observingVehicleId = vehicleId
        componentsJob?.cancel()
        if (vehicleId == null) {
            _state.update {
                it.copy(
                    availableComponents = emptyList(),
                    selectedComponentTrackedId = null,
                    isLoadingComponents = false,
                )
            }
            return
        }
        _state.update { it.copy(isLoadingComponents = true) }
        componentsJob = viewModelScope.launch {
            combine(
                observeTrackedComponents(vehicleId),
                observeComponentCatalogAll(),
            ) { tracked, catalog ->
                buildOptions(vehicleId, tracked, catalog)
            }.collect { options ->
                _state.update { current ->
                    val available = options.map { it.trackedId }.toSet()
                    val preferredId = current.preselectedTrackedComponentId
                        ?.takeIf { it in available }
                    val keepId = current.selectedComponentTrackedId?.takeIf { it in available }
                    val nextSelectedTrackedId = preferredId ?: keepId
                    val needsAutofill = nextSelectedTrackedId != null &&
                        nextSelectedTrackedId != current.selectedComponentTrackedId
                    val updated = current.copy(
                        availableComponents = options,
                        selectedComponentTrackedId = nextSelectedTrackedId,
                        isLoadingComponents = false,
                    )
                    if (needsAutofill && updated.mode == Mode.Komponen) {
                        applyKomponenAutofill(updated)
                    } else {
                        updated
                    }
                }
            }
        }
    }

    private fun buildOptions(
        vehicleId: String,
        tracked: List<TrackedComponent>,
        catalog: List<Component>,
    ): List<ComponentOption> {
        val vehicle = _state.value.vehicles.firstOrNull { it.id == vehicleId }
        val type = vehicle?.type
        val byId = catalog.associateBy { it.id }
        return tracked.mapNotNull { t ->
            val cat = byId[t.catalogComponentId] ?: return@mapNotNull null
            val interval = type?.let { cat.intervalFor(it) }
            ComponentOption(
                trackedId = t.id,
                catalogId = cat.id,
                label = t.customName ?: cat.label,
                iconKey = cat.iconKey,
                colorHex = cat.colorHex,
                intervalKm = t.intervalKmOverride ?: interval?.distance?.kilometers,
                intervalDays = t.intervalDaysOverride ?: interval?.durationDays,
                intervalLabel = interval?.displayLabel ?: "—",
            )
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
                preselectedTrackedComponentId = trackedComponentId ?: current.preselectedTrackedComponentId,
                mode = if (trackedComponentId != null) Mode.Komponen else current.mode,
            )
        }
        refreshComponentsObservation(_state.value.selectedVehicleId)
    }

    fun selectVehicle(vehicleId: String) {
        _state.update { current ->
            if (current.selectedVehicleId == vehicleId) return@update current
            current.copy(
                selectedVehicleId = vehicleId,
                selectedComponentTrackedId = null,
            )
        }
        refreshComponentsObservation(vehicleId)
    }

    fun setMode(mode: Mode) {
        _state.update { current ->
            if (current.mode == mode) return@update current
            val updated = current.copy(mode = mode)
            when (mode) {
                Mode.Komponen -> if (updated.selectedComponent != null) applyKomponenAutofill(updated) else updated
                Mode.Manual -> updated.copy(triggerMode = ReminderTriggerMode.Both)
            }
        }
    }

    fun selectComponent(trackedId: String) {
        _state.update { current ->
            if (current.availableComponents.none { it.trackedId == trackedId }) return@update current
            val updated = current.copy(selectedComponentTrackedId = trackedId)
            applyKomponenAutofill(updated)
        }
    }

    private fun applyKomponenAutofill(state: UiState): UiState {
        val option = state.selectedComponent ?: return state
        val vehicle = state.selectedVehicle
        val nowMillis = clock.nowEpochMillis()
        val baseKm = vehicle?.odometer?.kilometers ?: 0L
        val intervalKm = option.intervalKm
        val intervalDays = option.intervalDays
        val nextKm = intervalKm?.let { baseKm + it }
        val nextDate = intervalDays?.let { nowMillis + it * MILLIS_PER_DAY }
        val triggerMode = when {
            nextKm != null && nextDate != null -> ReminderTriggerMode.Both
            nextKm != null -> ReminderTriggerMode.Km
            nextDate != null -> ReminderTriggerMode.Date
            else -> ReminderTriggerMode.Date
        }
        return state.copy(
            title = option.label,
            targetKm = nextKm,
            targetDateMillis = nextDate ?: state.targetDateMillis.takeIf { it > 0L } ?: nowMillis,
            triggerMode = triggerMode,
        )
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
        val title = snapshot.title.trim().ifBlank { snapshot.selectedComponent?.label ?: "Pengingat servis" }
        val serviceType = when (snapshot.mode) {
            Mode.Komponen -> snapshot.selectedComponent?.let { deriveServiceType(it.catalogId) } ?: ServiceType.Other
            Mode.Manual -> ServiceType.Other
        }
        val linkedTrackedId = when (snapshot.mode) {
            Mode.Komponen -> snapshot.selectedComponent?.trackedId
            Mode.Manual -> null
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val draft = ReminderDraft(
                    vehicleId = vehicle.id,
                    serviceType = serviceType,
                    trackedComponentId = linkedTrackedId,
                    title = title,
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

    private fun buildTrigger(s: UiState): ReminderTrigger? {
        val effectiveMode = when (s.mode) {
            Mode.Komponen -> when {
                (s.targetKm ?: 0L) > 0L && s.targetDateMillis > 0L -> ReminderTriggerMode.Both
                (s.targetKm ?: 0L) > 0L -> ReminderTriggerMode.Km
                s.targetDateMillis > 0L -> ReminderTriggerMode.Date
                else -> return null
            }
            Mode.Manual -> s.triggerMode
        }
        return when (effectiveMode) {
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

    private fun deriveServiceType(catalogId: String): ServiceType = when (catalogId) {
        "oli_mesin", "oli_gardan", "oli_gardan_mobil", "oli_transmisi" -> ServiceType.OilChange
        "filter_oli", "filter_udara", "filter_ac", "filter_cvt" -> ServiceType.Filter
        "ban" -> ServiceType.Tire
        "aki" -> ServiceType.Battery
        "kampas_rem", "minyak_rem" -> ServiceType.Brake
        "radiator" -> ServiceType.Radiator
        "busi", "tune_up" -> ServiceType.TuneUp
        else -> ServiceType.Other
    }

    private companion object {
        const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
    }
}
