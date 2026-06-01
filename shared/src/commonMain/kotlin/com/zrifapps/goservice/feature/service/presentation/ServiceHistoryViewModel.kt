package com.zrifapps.goservice.feature.service.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.paging.PageRequest
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.domain.usecase.ObserveServiceChanges
import com.zrifapps.goservice.feature.service.domain.usecase.PageServiceHistory
import com.zrifapps.goservice.feature.service.domain.usecase.SumServiceCost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * Drives the service history list with SQLite-backed paging instead of holding the
 * whole table in memory. A single growing window (capped at [PageRequest.MAX_LIMIT])
 * is fetched via [PageServiceHistory]; [ObserveServiceChanges] re-runs it on any write
 * so the list stays live for add/edit/delete. Filtering (vehicles, query, time range)
 * and the total cost summary are computed in SQL over the full filtered set.
 */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ServiceHistoryViewModel(
    private val pageServiceHistory: PageServiceHistory,
    private val sumServiceCost: SumServiceCost,
    private val observeServiceChanges: ObserveServiceChanges,
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
        val canLoadMore: Boolean get() = records.size < totalCount && pageSize < PageRequest.MAX_LIMIT
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
            pageSizeFlow,
        ) { filter, sort, pageSize -> Request(filter, sort, pageSize) }
            // Re-emit the current request on every write so the page reloads reactively.
            .combine(observeServiceChanges()) { request, _ -> request }
            .mapLatest { request -> load(request) }
            .onEach { result ->
                _state.update { current ->
                    if (result == null) {
                        current.copy(isLoading = false)
                    } else {
                        current.copy(
                            records = result.records,
                            totalCount = result.total,
                            totalCostIdr = result.totalCost,
                            pageSize = result.pageSize,
                            isLoading = false,
                        )
                    }
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
        pageSizeFlow.update { (it + PAGE_INCREMENT).coerceAtMost(PageRequest.MAX_LIMIT) }
    }

    fun resetWindow() {
        pageSizeFlow.value = INITIAL_PAGE_SIZE
    }

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)

    private suspend fun load(request: Request): LoadResult? {
        val page = pageServiceHistory(
            filter = request.filter,
            sort = request.sort,
            page = PageRequest(offset = 0, limit = request.pageSize),
        )
        return when (page) {
            is DomainResult.Success -> {
                val totalCost = when (val cost = sumServiceCost(request.filter)) {
                    is DomainResult.Success -> cost.data
                    is DomainResult.Failure -> 0L
                }
                LoadResult(
                    records = page.data.items,
                    total = page.data.total,
                    totalCost = totalCost,
                    pageSize = request.pageSize,
                )
            }

            is DomainResult.Failure -> null
        }
    }

    private data class Request(
        val filter: ServiceFilter,
        val sort: ServiceSort,
        val pageSize: Int,
    )

    private data class LoadResult(
        val records: List<ServiceRecord>,
        val total: Int,
        val totalCost: Long,
        val pageSize: Int,
    )

    private companion object {
        const val INITIAL_PAGE_SIZE = 20
        const val PAGE_INCREMENT = 20
        const val QUERY_DEBOUNCE_MS = 200L
    }
}
