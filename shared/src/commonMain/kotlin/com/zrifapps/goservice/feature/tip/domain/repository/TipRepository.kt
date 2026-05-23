// DEFERRED: Tip feature is hidden for MVP. Content will come from a network API
// in a later release. Do NOT build new features on top of this layer yet.
package com.zrifapps.goservice.feature.tip.domain.repository

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.feature.tip.domain.model.Tip
import com.zrifapps.goservice.feature.tip.domain.model.TipCategory
import com.zrifapps.goservice.feature.tip.domain.model.TipFilter
import kotlinx.coroutines.flow.Flow

interface TipRepository {

    fun observe(filter: TipFilter = TipFilter()): Flow<List<Tip>>

    fun observeFeatured(): Flow<Tip?>

    fun observeCategories(): Flow<List<TipCategory>>

    suspend fun getById(id: String): DomainResult<Tip>

    suspend fun refresh(): DomainResult<Unit>
}
