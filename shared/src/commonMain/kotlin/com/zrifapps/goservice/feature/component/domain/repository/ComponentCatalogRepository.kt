package com.zrifapps.goservice.feature.component.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import kotlinx.coroutines.flow.Flow

interface ComponentCatalogRepository {

    fun observeAll(): Flow<List<Component>>

    fun observeBySubtype(type: VehicleType, subtypeId: String): Flow<List<Component>>

    suspend fun getById(id: String): DomainResult<Component>

    suspend fun upsertCustom(component: Component): DomainResult<Component>

    suspend fun softDeleteCustom(id: String): DomainResult<Unit>

    suspend fun refreshFromRemote(): DomainResult<Unit>
}
