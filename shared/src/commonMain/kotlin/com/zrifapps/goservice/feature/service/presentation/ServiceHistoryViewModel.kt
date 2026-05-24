package com.zrifapps.goservice.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveServiceHistory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ServiceHistoryViewModel(
    private val observeServiceHistory: ObserveServiceHistory,
) : ViewModel() {

    data class UiState(
        val records: List<ServiceRecord> = emptyList(),
        val totalCount: Int = 0,
        val totalCostIdr: Long = 0L,
        val pageSize: Int = INITIAL_PAGE_SIZE,
        val isLoading: Boolean = true,
        val vehicleIds: Set<String> = emptySet(),
        val query: String = "",
        val sort: ServiceSort = ServiceSort.DateDesc,
    ) {
        val isEmpty: Boolean get() = !isLoading && totalCount == 0 &&
            vehicleIds.isEmpty() && query.isBlank()
        val canLoadMore: Boolean get() = records.size < totalCount
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val filterFlow = MutableStateFlow(ServiceFilter())
    private val sortFlow = MutableStateFlow(ServiceSort.DateDesc)
    private val pageSizeFlow = MutableStateFlow(INITIAL_PAGE_SIZE)

    init {
        combine(
            filterFlow.debounce { f ->
                if (f.query.isNullOrBlank()) 0L else QUERY_DEBOUNCE_MS
            },
            sortFlow,
        ) { filter, sort -> filter to sort }
            .flatMapLatest { (filter, sort) ->
                observeServiceHistory(ObserveServiceHistory.Params(filter = filter, sort = sort))
                    .map { records -> records }
            }
            .combine(pageSizeFlow) { records, pageSize ->
                Window(records, pageSize)
            }
            .onEach { (records, pageSize) ->
                _state.update {
                    it.copy(
                        records = records.take(pageSize),
                        totalCount = records.size,
                        totalCostIdr = records.sumOf { rec -> rec.cost.amountIdr },
                        pageSize = pageSize,
                        isLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun setVehicleIds(ids: Set<String>) {
        _state.update { it.copy(vehicleIds = ids) }
        filterFlow.update { it.copy(vehicleIds = ids) }
        resetWindow()
    }

    fun setQuery(query: String) {
        _state.update { it.copy(query = query) }
        filterFlow.update { it.copy(query = query) }
        resetWindow()
    }

    fun setSort(sort: ServiceSort) {
        _state.update { it.copy(sort = sort) }
        sortFlow.value = sort
        resetWindow()
    }

    fun loadMore() {
        if (!_state.value.canLoadMore) return
        pageSizeFlow.update { it + PAGE_INCREMENT }
    }

    fun resetWindow() {
        pageSizeFlow.value = INITIAL_PAGE_SIZE
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    private data class Window(val records: List<ServiceRecord>, val pageSize: Int)

    private companion object {
        const val INITIAL_PAGE_SIZE = 20
        const val PAGE_INCREMENT = 20
        const val QUERY_DEBOUNCE_MS = 200L
    }
}
