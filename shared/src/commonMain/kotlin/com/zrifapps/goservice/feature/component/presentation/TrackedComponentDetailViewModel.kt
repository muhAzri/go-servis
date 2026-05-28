package com.zrifapps.goservice.feature.component.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.GetCatalogComponent
import com.zrifapps.goservice.feature.component.domain.usecase.ObserveTrackedComponent
import com.zrifapps.goservice.feature.component.domain.usecase.UntrackComponent
import com.zrifapps.goservice.feature.component.domain.usecase.UpdateTrackedComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackedComponentDetailViewModel(
    private val observeTrackedComponent: ObserveTrackedComponent,
    private val getCatalogComponent: GetCatalogComponent,
    private val updateTrackedComponent: UpdateTrackedComponent,
    private val untrackComponent: UntrackComponent,
) : ViewModel() {

    enum class IntervalMode { Preset, Custom }

    data class UiState(
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val tracked: TrackedComponent? = null,
        val catalog: Component? = null,
        val mode: IntervalMode = IntervalMode.Preset,
        val customKmOverride: Long? = null,
        val customDaysOverride: Int? = null,
    )

    sealed interface Event {
        data object Saved : Event
        data object Stopped : Event
        data class Failed(val error: DomainError) : Event
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var observeJob: Job? = null
    private var loadedId: String? = null
    private var catalogLoaded: String? = null

    fun load(trackedId: String) {
        if (trackedId.isBlank()) return
        if (loadedId == trackedId) return
        loadedId = trackedId
        _state.update { UiState(isLoading = true) }
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeTrackedComponent(trackedId).collect { tracked ->
                _state.update { current ->
                    val mode = if (tracked?.intervalKmOverride != null || tracked?.intervalDaysOverride != null) {
                        IntervalMode.Custom
                    } else {
                        IntervalMode.Preset
                    }
                    current.copy(
                        isLoading = false,
                        tracked = tracked,
                        mode = if (current.tracked == null) mode else current.mode,
                        customKmOverride = current.customKmOverride ?: tracked?.intervalKmOverride,
                        customDaysOverride = current.customDaysOverride ?: tracked?.intervalDaysOverride,
                    )
                }
                tracked?.catalogComponentId?.let { ensureCatalogLoaded(it) }
            }
        }
    }

    private fun ensureCatalogLoaded(catalogId: String) {
        if (catalogLoaded == catalogId) return
        catalogLoaded = catalogId
        viewModelScope.launch {
            when (val r = getCatalogComponent(catalogId)) {
                is DomainResult.Success -> _state.update { it.copy(catalog = r.data) }
                is DomainResult.Failure -> Unit
            }
        }
    }

    fun setMode(mode: IntervalMode) {
        _state.update { it.copy(mode = mode) }
    }

    fun setCustomKm(km: Long?) {
        _state.update { it.copy(customKmOverride = km?.coerceAtLeast(0L)) }
    }

    fun setCustomDays(days: Int?) {
        _state.update { it.copy(customDaysOverride = days?.coerceAtLeast(0)) }
    }

    fun save() {
        val snapshot = _state.value
        val tracked = snapshot.tracked ?: return
        if (snapshot.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val updated = when (snapshot.mode) {
                    IntervalMode.Preset -> tracked.copy(
                        intervalKmOverride = null,
                        intervalDaysOverride = null,
                    )
                    IntervalMode.Custom -> tracked.copy(
                        intervalKmOverride = snapshot.customKmOverride,
                        intervalDaysOverride = snapshot.customDaysOverride,
                    )
                }
                when (val r = updateTrackedComponent(updated)) {
                    is DomainResult.Success -> _events.emit(Event.Saved)
                    is DomainResult.Failure -> _events.emit(Event.Failed(r.error))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun stopMonitoring() {
        val tracked = _state.value.tracked ?: return
        if (_state.value.isSaving) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                when (val r = untrackComponent(tracked.id)) {
                    is DomainResult.Success -> _events.emit(Event.Stopped)
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
}
