package com.zrifapps.goservice.feature.component.data.repository

import com.zrifapps.goservice.core.data.runStorage
import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.toDomain
import com.zrifapps.goservice.feature.component.data.local.toEntity
import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ComponentCatalogRepositoryImpl(
    private val dao: ComponentDao,
    private val clock: AppClock,
) : ComponentCatalogRepository {

    override fun observeAll(): Flow<List<Component>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeBySubtype(type: VehicleType, subtypeId: String): Flow<List<Component>> =
        dao.observeAll().map { list ->
            list.map { it.toDomain() }.filter { it.matches(subtypeId) }
        }

    override suspend fun getById(id: String): DomainResult<Component> {
        val entity = dao.getById(id) ?: return DomainResult.Failure(
            DomainError.NotFound(resource = "Component", id = id)
        )
        return DomainResult.Success(entity.toDomain())
    }

    override suspend fun upsertCustom(component: Component): DomainResult<Component> {
        val now = clock.nowEpochMillis()
        val customized = component.copy(isCustom = true, updatedAt = now)
        return runStorage {
            dao.upsert(customized.toEntity())
            customized
        }
    }

    override suspend fun softDeleteCustom(id: String): DomainResult<Unit> = runStorage {
        dao.deleteCustom(id)
        Unit
    }

    override suspend fun refreshFromRemote(): DomainResult<Unit> = DomainResult.Success(Unit)
}
