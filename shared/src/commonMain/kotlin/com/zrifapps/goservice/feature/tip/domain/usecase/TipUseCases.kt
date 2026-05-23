// DEFERRED: Tip feature is hidden for MVP. Content will come from a network API
// in a later release. Do NOT build new features on top of this layer yet.
package com.zrifapps.goservice.feature.tip.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.feature.tip.domain.model.Tip
import com.zrifapps.goservice.feature.tip.domain.model.TipCategory
import com.zrifapps.goservice.feature.tip.domain.model.TipFilter
import com.zrifapps.goservice.feature.tip.domain.repository.TipRepository
import kotlinx.coroutines.flow.Flow

class ObserveTips(
    private val repository: TipRepository,
) {
    operator fun invoke(filter: TipFilter = TipFilter()): Flow<List<Tip>> = repository.observe(filter)
}

class ObserveFeaturedTip(
    private val repository: TipRepository,
) {
    operator fun invoke(): Flow<Tip?> = repository.observeFeatured()
}

class ObserveTipCategories(
    private val repository: TipRepository,
) {
    operator fun invoke(): Flow<List<TipCategory>> = repository.observeCategories()
}

class GetTip(
    private val repository: TipRepository,
) : UseCase<String, Tip> {
    override suspend fun invoke(params: String): DomainResult<Tip> = repository.getById(params)
}
