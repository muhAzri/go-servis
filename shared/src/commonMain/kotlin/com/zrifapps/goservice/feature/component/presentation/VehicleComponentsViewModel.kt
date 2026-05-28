package com.zrifapps.goservice.feature.component.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalogAll
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleComponentsViewModel(
    private val vehicleRepository: VehicleRepository,
    private val observeTrackedComponents: ObserveTrackedComponents,
    private val observeComponentCatalogAll: ObserveComponentCatalogAll,
) : ViewModel() {

    data class TrackedItem(
        val tracked: TrackedComponent,
        val catalog: Component?,
    ) {
        val displayName: String get() = tracked.displayName(catalog)
        val urgency: ComponentUrgency get() = tracked.urgency
    }

    data class UiState(
        val isLoading: Boolean = true,
        val vehicleId: String? = null,
        val vehicle: Vehicle? = null,
        val items: List<TrackedItem> = emptyList(),
    ) {
        val isEmpty: Boolean get() = !isLoading && items.isEmpty()
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var loadedId: String? = null

    fun load(vehicleId: String) {
        if (vehicleId.isBlank()) return
        if (loadedId == vehicleId) return
        loadedId = vehicleId
        _state.update { it.copy(isLoading = true, vehicleId = vehicleId) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                vehicleRepository.observeVehicle(vehicleId),
                observeTrackedComponents(vehicleId),
                observeComponentCatalogAll(),
            ) { vehicle, tracked, catalog ->
                val catalogById = catalog.associateBy { it.id }
                val items = tracked.map { t ->
                    TrackedItem(tracked = t, catalog = catalogById[t.catalogComponentId])
                }
                Triple(vehicle, items, false)
            }.collect { (vehicle, items, _) ->
                _state.update {
                    it.copy(isLoading = false, vehicle = vehicle, items = items)
                }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)
}
