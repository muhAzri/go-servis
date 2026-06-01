package com.zrifapps.goservice.feature.service.domain.usecase

import com.zrifapps.goservice.core.paging.Page
import com.zrifapps.goservice.core.paging.PageRequest
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow

/** Loads one filtered, sorted page of service history straight from storage. */
class PageServiceHistory(
    private val repository: ServiceRepository,
) {
    suspend operator fun invoke(
        filter: ServiceFilter,
        sort: ServiceSort,
        page: PageRequest,
    ): DomainResult<Page<ServiceRecord>> = repository.query(filter, sort, page)
}

/** Sums cost across the whole filtered set so the summary stays accurate while paging. */
class SumServiceCost(
    private val repository: ServiceRepository,
) {
    suspend operator fun invoke(filter: ServiceFilter): DomainResult<Long> =
        repository.sumCost(filter)
}

/** Emits whenever service records change, used to re-run the current page reactively. */
class ObserveServiceChanges(
    private val repository: ServiceRepository,
) {
    operator fun invoke(): Flow<Unit> = repository.observeChanges()
}
