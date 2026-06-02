package com.zrifapps.goservice.feature.vehicle.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveVehicleServiceHistory
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import com.zrifapps.goservice.feature.vehicle.domain.usecase.ObserveVehicles
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VehicleDetailViewModel(
    private val vehicleRepository: VehicleRepository,
    private val observeVehicles: ObserveVehicles,
    private val observeVehicleServiceHistory: ObserveVehicleServiceHistory,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val vehicle: Vehicle? = null,
        val recentServices: List<ServiceRecord> = emptyList(),
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var servicesJob: Job? = null
    private var loadedKey: String? = null
    private var observingVehicleId: String? = null

    fun load(vehicleId: String?) {
        val key = vehicleId ?: FALLBACK_KEY
        if (loadedKey == key) return
        loadedKey = key
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            if (vehicleId != null) {
                vehicleRepository.observeVehicle(vehicleId).collect { vehicle ->
                    _state.update { it.copy(isLoading = false, vehicle = vehicle) }
                    refreshServicesObservation(vehicle?.id)
                }
            } else {
                observeVehicles().collect { list ->
                    val first = list.firstOrNull()
                    _state.update { it.copy(isLoading = false, vehicle = first) }
                    refreshServicesObservation(first?.id)
                }
            }
        }
    }

    private fun refreshServicesObservation(vehicleId: String?) {
        if (vehicleId == observingVehicleId) return
        observingVehicleId = vehicleId
        servicesJob?.cancel()
        if (vehicleId == null) {
            _state.update { it.copy(recentServices = emptyList()) }
            return
        }
        servicesJob = viewModelScope.launch {
            observeVehicleServiceHistory(vehicleId)
                .map { records -> records.sortedByDescending { it.serviceDate }.take(MAX_RECENT) }
                .collect { recent ->
                    _state.update { it.copy(recentServices = recent) }
                }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    private companion object {
        const val FALLBACK_KEY = "__first__"
        const val MAX_RECENT = 3
    }
}
