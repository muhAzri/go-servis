package com.zrifapps.goservice.feature.component.domain.usecase

import com.zrifapps.goservice.feature.component.domain.model.Component
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import kotlinx.coroutines.flow.Flow

class ObserveComponentCatalog(
    private val repository: ComponentCatalogRepository,
) {
    data class Params(val type: VehicleType, val subtypeId: String)
    operator fun invoke(params: Params): Flow<List<Component>> =
        repository.observeBySubtype(params.type, params.subtypeId)
}
