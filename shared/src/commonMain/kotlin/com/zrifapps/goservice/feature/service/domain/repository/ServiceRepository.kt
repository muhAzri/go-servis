package com.zrifapps.goservice.feature.service.domain.repository

import com.zrifapps.goservice.core.paging.Page
import com.zrifapps.goservice.core.paging.PageRequest
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.service.domain.model.ServiceFilter
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecordDraft
import com.zrifapps.goservice.feature.service.domain.model.ServiceSort
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {

    fun observeRecords(
        filter: ServiceFilter = ServiceFilter(),
        sort: ServiceSort = ServiceSort.DateDesc,
    ): Flow<List<ServiceRecord>>

    fun observeOne(id: String): Flow<ServiceRecord?>

    fun observeForVehicle(vehicleId: String): Flow<List<ServiceRecord>>

    suspend fun getById(id: String): DomainResult<ServiceRecord>

    suspend fun query(
        filter: ServiceFilter,
        sort: ServiceSort,
        page: PageRequest,
    ): DomainResult<Page<ServiceRecord>>

    suspend fun create(draft: ServiceRecordDraft): DomainResult<ServiceRecord>

    suspend fun update(record: ServiceRecord): DomainResult<ServiceRecord>

    suspend fun softDelete(id: String): DomainResult<Unit>

    suspend fun refresh(): DomainResult<Unit>

    /** Inserts service records whose id is not already present; returns count inserted. */
    suspend fun importMissing(items: List<ServiceRecord>): Int
}
