package com.zrifapps.goservice.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveServiceRecord
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceDetailViewModel(
    private val observeServiceRecord: ObserveServiceRecord,
    private val vehicleRepository: VehicleRepository,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val record: ServiceRecord? = null,
        val vehicle: Vehicle? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var recordJob: Job? = null
    private var vehicleJob: Job? = null
    private var loadedId: String? = null
    private var loadedVehicleId: String? = null

    fun load(recordId: String) {
        if (loadedId == recordId) return
        loadedId = recordId
        recordJob?.cancel()
        recordJob = viewModelScope.launch {
            observeServiceRecord(recordId).collect { record ->
                _state.update { it.copy(isLoading = false, record = record) }
                val vehicleId = record?.vehicleId
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

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)
}
