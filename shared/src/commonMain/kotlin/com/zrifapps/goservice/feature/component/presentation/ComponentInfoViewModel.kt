package com.zrifapps.goservice.feature.component.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.ComponentTag
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponentDraft
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.component.domain.usecase.GetCatalogComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponents
import com.zrifapps.goservice.feature.component.domain.usecase.TrackComponentWithReminder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComponentInfoViewModel(
    private val getCatalogComponent: GetCatalogComponent,
    private val observeTrackedComponents: ObserveTrackedComponents,
    private val trackComponent: TrackComponentWithReminder,
    private val catalogRepository: ComponentCatalogRepository,
    private val clock: AppClock,
) : ViewModel() {

    enum class IntervalMode { Preset, Custom }

    data class UiState(
        val isLoading: Boolean = true,
        val isSubmitting: Boolean = false,
        val catalogId: String? = null,
        val vehicleId: String? = null,
        val catalog: Component? = null,
        val existingTrackedId: String? = null,
        val mode: IntervalMode = IntervalMode.Preset,
        val customKm: Long? = null,
        val customDays: Int? = null,
        /** Komponen kustom baru yang belum disimpan ke katalog (dibuat saat Pantau). */
        val pendingCustom: Boolean = false,
    ) {
        /** Komponen tanpa interval pabrikan (mis. custom) — user wajib atur sendiri. */
        val requiresCustomInterval: Boolean
            get() = catalog != null && catalog.intervalMotor == null && catalog.intervalMobil == null

        val canTrack: Boolean
            get() = when {
                catalog == null -> false
                requiresCustomInterval -> customKm != null || customDays != null
                else -> true
            }
    }

    sealed interface Event {
        data class Tracked(val trackedId: String) : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var loadedKey: String? = null
    private var trackingJob: Job? = null

    fun load(catalogId: String, vehicleId: String) {
        if (catalogId.isBlank() || vehicleId.isBlank()) return
        val key = "$catalogId|$vehicleId"
        if (loadedKey == key) return
        loadedKey = key
        _state.update { UiState(isLoading = true, catalogId = catalogId, vehicleId = vehicleId) }
        viewModelScope.launch {
            when (val r = getCatalogComponent(catalogId)) {
                is DomainResult.Success -> _state.update {
                    val noPreset = r.data.intervalMotor == null && r.data.intervalMobil == null
                    it.copy(
                        isLoading = false,
                        catalog = r.data,
                        // Komponen tanpa data pabrikan dipaksa atur sendiri.
                        mode = if (noPreset) IntervalMode.Custom else it.mode,
                    )
                }
                is DomainResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(Event.Failed(r.error))
                }
            }
        }
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            observeTrackedComponents(vehicleId).collect { list ->
                val match = list.firstOrNull { it.catalogComponentId == catalogId }
                _state.update { it.copy(existingTrackedId = match?.id) }
            }
        }
    }

    /**
     * Mode komponen kustom baru. Komponen sintetis dibuat di memori saja; baru
     * disimpan ke katalog + di-track saat user menekan Pantau (lihat [track]).
     */
    fun loadNewCustom(name: String, vehicleId: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || vehicleId.isBlank()) return
        val key = "new-custom:$trimmed|$vehicleId"
        if (loadedKey == key) return
        loadedKey = key
        trackingJob?.cancel()
        val now = clock.nowEpochMillis()
        val synthetic = Component(
            id = "custom:$vehicleId:$now",
            label = trimmed,
            iconKey = "WRENCH",
            colorHex = "#5C6357",
            intervalMotor = null,
            intervalMobil = null,
            why = "",
            applicableSubtypes = listOf(Component.UNIVERSAL_SUBTYPE),
            tag = ComponentTag.Plus,
            isCustom = true,
            createdAt = now,
            updatedAt = now,
        )
        _state.update {
            UiState(
                isLoading = false,
                catalogId = synthetic.id,
                vehicleId = vehicleId,
                catalog = synthetic,
                mode = IntervalMode.Custom,
                pendingCustom = true,
            )
        }
    }

    fun setMode(mode: IntervalMode) {
        _state.update { it.copy(mode = mode) }
    }

    fun setCustomKm(km: Long?) {
        _state.update { it.copy(customKm = km?.coerceAtLeast(0L)) }
    }

    fun setCustomDays(days: Int?) {
        _state.update { it.copy(customDays = days?.coerceAtLeast(0)) }
    }

    fun track() {
        val snapshot = _state.value
        val catalogId = snapshot.catalogId ?: return
        val vehicleId = snapshot.vehicleId ?: return
        val catalog = snapshot.catalog ?: return
        if (snapshot.isSubmitting) return
        if (!snapshot.canTrack) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            try {
                // Komponen kustom baru: simpan ke katalog dulu (sekarang, setelah dikonfirmasi).
                if (snapshot.pendingCustom) {
                    when (val upsert = catalogRepository.upsertCustom(catalog)) {
                        is DomainResult.Success -> Unit
                        is DomainResult.Failure -> {
                            _events.emit(Event.Failed(upsert.error))
                            return@launch
                        }
                    }
                }
                val draft = TrackedComponentDraft(
                    vehicleId = vehicleId,
                    catalogComponentId = catalogId,
                    customName = if (snapshot.pendingCustom) catalog.label else null,
                    intervalKmOverride = if (snapshot.mode == IntervalMode.Custom) snapshot.customKm else null,
                    intervalDaysOverride = if (snapshot.mode == IntervalMode.Custom) snapshot.customDays else null,
                )
                when (val r = trackComponent(draft)) {
                    is DomainResult.Success -> _events.emit(Event.Tracked(r.data.id))
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    fun observeEvents(onEvent: (Event) -> Unit): Cancellable =
        events.subscribeOn(viewModelScope, onEvent)
}
