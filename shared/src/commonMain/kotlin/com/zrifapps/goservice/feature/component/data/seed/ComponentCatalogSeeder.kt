package com.zrifapps.goservice.feature.component.data.seed

import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.toEntity
import kotlinx.coroutines.flow.first

class ComponentCatalogSeeder(
    private val dao: ComponentDao,
    private val clock: AppClock,
) {

    suspend fun seedIfNeeded() {
        val existingIds = dao.observeAll().first().map { it.id }.toSet()
        val now = clock.nowEpochMillis()
        val defaults = DefaultComponentCatalog.all(now)
        val missing = defaults.filter { it.id !in existingIds }
        if (missing.isEmpty()) return
        dao.upsertAll(missing.map { it.toEntity() })
    }
}
