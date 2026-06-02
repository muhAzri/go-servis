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
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalogAll
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.component.domain.usecase.ResolveComponentServiceCycle
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.CompleteReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceKind
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecordDraft
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.service.domain.usecase.RecordService
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
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

class AddServiceViewModel(
    observeVehicles: ObserveVehicles,
    private val recordService: RecordService,
    private val completeReminder: CompleteReminder,
    private val resolveComponentServiceCycle: ResolveComponentServiceCycle,
    private val observeTrackedComponents: ObserveTrackedComponents,
    private val observeComponentCatalogAll: ObserveComponentCatalogAll,
    private val addReminder: AddReminder,
    private val clock: AppClock,
) : ViewModel() {

    data class ComponentOption(
        val id: String,
        val label: String,
        val iconKey: String,
        val colorHex: String,
        val intervalLabel: String,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val vehicles: List<Vehicle> = emptyList(),
        val selectedVehicleId: String? = null,
        val mode: ServiceKind = ServiceKind.Komponen,
        val customTitle: String = "",
        val serviceDateMillis: Long = 0L,
        val odometerKm: Long? = null,
        val workshop: String = "",
        val costIdr: Long = 0L,
        val note: String = "",
        val availableComponents: List<ComponentOption> = emptyList(),
        val isLoadingComponents: Boolean = false,
        val selectedComponentIds: Set<String> = emptySet(),
        val sourceReminderId: String? = null,
        val trackedComponentId: String? = null,
    ) {
        val selectedVehicle: Vehicle? get() = vehicles.firstOrNull { it.id == selectedVehicleId }
        val hasTrackedComponents: Boolean get() = availableComponents.isNotEmpty()

        val canSave: Boolean
            get() = !isLoading && !isSaving &&
                selectedVehicle != null &&
                serviceDateMillis > 0L &&
                odometerKm != null && odometerKm >= 0L &&
                when (mode) {
                    ServiceKind.Komponen -> selectedComponentIds.isNotEmpty()
                    ServiceKind.Rutin -> true
                    ServiceKind.Manual -> customTitle.trim().isNotEmpty()
                }
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
                    val newOdometer = current.odometerKm
                        ?: list.firstOrNull { it.id == newSelectedId }?.odometer?.kilometers
                    current.copy(
                        isLoading = false,
                        vehicles = list,
                        selectedVehicleId = newSelectedId,
                        odometerKm = newOdometer,
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
                    selectedComponentIds = emptySet(),
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
                    val available = options.map { it.id }.toSet()
                    val pruned = current.selectedComponentIds.intersect(available)
                    current.copy(
                        availableComponents = options,
                        selectedComponentIds = pruned,
                        isLoadingComponents = false,
                    )
                }
            }
        }
    }

    private fun buildOptions(
        vehicleId: String,
        tracked: List<TrackedComponent>,
        catalog: List<Component>,
    ): List<ComponentOption> {
        val vehicleType = _state.value.vehicles.firstOrNull { it.id == vehicleId }?.type
        val byId = catalog.associateBy { it.id }
        return tracked.mapNotNull { t ->
            val cat = byId[t.catalogComponentId] ?: return@mapNotNull null
            ComponentOption(
                id = cat.id,
                label = t.customName ?: cat.label,
                iconKey = cat.iconKey,
                colorHex = cat.colorHex,
                intervalLabel = vehicleType?.let { cat.intervalFor(it)?.displayLabel } ?: "—",
            )
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
        refreshComponentsObservation(_state.value.selectedVehicleId)
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
        refreshComponentsObservation(vehicleId)
    }

    fun setMode(mode: ServiceKind) {
        _state.update { it.copy(mode = mode) }
    }

    fun setCustomTitle(value: String) {
        _state.update { it.copy(customTitle = value) }
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
            if (current.availableComponents.none { it.id == componentId }) return@update current
            val next = current.selectedComponentIds.toMutableSet().apply {
                if (!add(componentId)) remove(componentId)
            }
            current.copy(selectedComponentIds = next)
        }
    }

    fun setComponents(ids: Set<String>) {
        _state.update { current ->
            val available = current.availableComponents.map { it.id }.toSet()
            current.copy(selectedComponentIds = ids.intersect(available))
        }
    }

    fun submit() {
        val snapshot = _state.value
        val vehicle = snapshot.selectedVehicle ?: return
        val km = snapshot.odometerKm ?: return
        if (snapshot.isSaving || snapshot.serviceDateMillis <= 0L) return
        if (!snapshot.canSave) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val componentsForRecord = when (snapshot.mode) {
                    ServiceKind.Komponen -> snapshot.selectedComponentIds.toList()
                    ServiceKind.Rutin, ServiceKind.Manual -> emptyList()
                }
                val customTitle = when (snapshot.mode) {
                    ServiceKind.Manual -> snapshot.customTitle.trim().ifBlank { null }
                    ServiceKind.Rutin -> "Servis Rutin"
                    ServiceKind.Komponen -> null
                }
                val derivedType = when (snapshot.mode) {
                    ServiceKind.Komponen -> deriveServiceType(snapshot.selectedComponentIds)
                    ServiceKind.Rutin -> ServiceType.TuneUp
                    ServiceKind.Manual -> ServiceType.Other
                }
                val draft = ServiceRecordDraft(
                    vehicleId = vehicle.id,
                    serviceType = derivedType,
                    kind = snapshot.mode,
                    customTitle = customTitle,
                    serviceDate = snapshot.serviceDateMillis,
                    odometer = Distance.ofKm(km),
                    workshop = snapshot.workshop.trim().ifBlank { null },
                    cost = Money.ofIdr(snapshot.costIdr),
                    note = snapshot.note.trim().ifBlank { null },
                    componentIds = componentsForRecord,
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
                        if (snapshot.mode == ServiceKind.Komponen) {
                            snapshot.trackedComponentId?.let { trackedComponentId ->
                                resolveComponentServiceCycle(
                                    ResolveComponentServiceCycle.Params(
                                        trackedComponentId = trackedComponentId,
                                        serviceDate = snapshot.serviceDateMillis,
                                        serviceOdometer = Distance.ofKm(km),
                                        serviceRecordId = recordId,
                                    ),
                                )
                            }
                        }
                        if (snapshot.mode == ServiceKind.Rutin) {
                            scheduleRutinReminder(vehicle, snapshot.serviceDateMillis, km)
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

    private suspend fun scheduleRutinReminder(vehicle: Vehicle, serviceDate: Long, km: Long) {
        val (intervalKm, intervalDays) = routineIntervalFor(vehicle.type)
        val targetDate = serviceDate + intervalDays * MILLIS_PER_DAY
        val targetKm = km + intervalKm
        val draft = ReminderDraft(
            vehicleId = vehicle.id,
            serviceType = ServiceType.TuneUp,
            trackedComponentId = null,
            title = "Servis Rutin Berikutnya",
            trigger = ReminderTrigger.ByBoth(
                targetOdometer = Distance.ofKm(targetKm),
                targetDate = targetDate,
            ),
            note = "Auto-dibuat dari catatan servis rutin.",
        )
        addReminder(draft)
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)

    private fun deriveServiceType(componentIds: Set<String>): ServiceType {
        val first = componentIds.firstOrNull() ?: return ServiceType.Other
        return when (first) {
            "oli_mesin", "oli_gardan", "oli_gardan_mobil", "oli_transmisi" -> ServiceType.OilChange
            "filter_oli", "filter_udara", "filter_ac", "filter_cvt" -> ServiceType.Filter
            "ban" -> ServiceType.Tire
            "aki" -> ServiceType.Battery
            "kampas_rem", "minyak_rem" -> ServiceType.Brake
            "radiator" -> ServiceType.Radiator
            "busi", "tune_up" -> ServiceType.TuneUp
            else -> ServiceType.Other
        }
    }

    private fun routineIntervalFor(type: VehicleType): Pair<Long, Int> = when (type) {
        VehicleType.Motor -> 4_000L to 180   // 4.000 km / 6 bulan
        VehicleType.Mobil -> 10_000L to 180  // 10.000 km / 6 bulan
    }

    private companion object {
        const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L
    }
}
