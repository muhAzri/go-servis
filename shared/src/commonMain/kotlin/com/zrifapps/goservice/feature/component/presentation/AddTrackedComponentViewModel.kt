package com.zrifapps.goservice.feature.component.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveComponentCatalogAll
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Penyedia daftar untuk layar "Tambah komponen": menampilkan rekomendasi sesuai
 * subtipe kendaraan + pencarian. Pemilihan komponen dilakukan satu per satu —
 * tap sebuah komponen membuka layar info/atur interval untuk dikonfirmasi di sana.
 * Tidak menulis apa pun ke DB; komponen kustom baru ditangani saat konfirmasi di
 * layar info (lihat ComponentInfoViewModel) supaya typo tak menyampah katalog.
 */
class AddTrackedComponentViewModel(
    private val vehicleRepository: VehicleRepository,
    private val observeComponentCatalogAll: ObserveComponentCatalogAll,
    private val observeTrackedComponents: ObserveTrackedComponents,
) : ViewModel() {

    data class Item(
        val component: Component,
        val alreadyTracked: Boolean,
    )

    data class UiState(
        val isLoading: Boolean = true,
        val vehicleId: String? = null,
        val vehicle: Vehicle? = null,
        val query: String = "",
        val items: List<Item> = emptyList(),
    ) {
        val showCustomPrompt: Boolean
            get() = query.isNotBlank() &&
                items.none { it.component.label.equals(query.trim(), ignoreCase = true) }
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var observeJob: Job? = null
    private var loadedId: String? = null

    private var lastVehicle: Vehicle? = null
    private var lastCatalog: List<Component> = emptyList()
    private var lastTracked: Set<String> = emptySet()

    fun load(vehicleId: String) {
        if (vehicleId.isBlank()) return
        if (loadedId == vehicleId) return
        loadedId = vehicleId
        _state.update { it.copy(isLoading = true, vehicleId = vehicleId) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            combine(
                vehicleRepository.observeVehicle(vehicleId),
                observeComponentCatalogAll(),
                observeTrackedComponents(vehicleId),
            ) { vehicle, catalog, tracked ->
                Triple(vehicle, catalog, tracked.map { it.catalogComponentId }.toSet())
            }.collect { (vehicle, catalog, trackedIds) ->
                lastVehicle = vehicle
                lastCatalog = catalog
                lastTracked = trackedIds
                _state.update { current ->
                    current.copy(
                        isLoading = false,
                        vehicle = vehicle,
                        items = buildItems(vehicle, catalog, trackedIds, current.query),
                    )
                }
            }
        }
    }

    fun setQuery(query: String) {
        _state.update { current ->
            current.copy(
                query = query,
                items = buildItems(lastVehicle, lastCatalog, lastTracked, query),
            )
        }
    }

    private fun buildItems(
        vehicle: Vehicle?,
        catalog: List<Component>,
        trackedIds: Set<String>,
        query: String,
    ): List<Item> {
        val q = query.trim().lowercase()
        val subtypeId = vehicle?.subtypeId?.takeIf { it.isNotBlank() } ?: Component.UNIVERSAL_SUBTYPE
        val type = vehicle?.type
        val base = if (q.isEmpty()) {
            catalog.filter { relevant(it, subtypeId, type) }
        } else {
            catalog.filter { it.label.lowercase().contains(q) }
        }
        return base
            .sortedWith(compareBy({ tagRank(it.tag) }, { it.label }))
            .map { Item(component = it, alreadyTracked = it.id in trackedIds) }
    }

    private fun relevant(component: Component, subtypeId: String, type: VehicleType?): Boolean {
        if (subtypeId != Component.UNIVERSAL_SUBTYPE) return component.matches(subtypeId)
        return when (type) {
            VehicleType.Mobil -> component.intervalMobil != null
            VehicleType.Motor -> component.intervalMotor != null
            null -> true
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    private companion object {
        fun tagRank(tag: ComponentTag): Int = when (tag) {
            ComponentTag.Core -> 0
            ComponentTag.Plus -> 1
            ComponentTag.Pro -> 2
        }
    }
}
