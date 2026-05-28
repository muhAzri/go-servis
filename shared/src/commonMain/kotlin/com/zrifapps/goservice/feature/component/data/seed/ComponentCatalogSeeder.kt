package com.zrifapps.goservice.feature.component.data.seed

import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.component.data.local.ComponentDao
import com.zrifapps.goservice.feature.component.data.local.toEntity

class ComponentCatalogSeeder(
    private val dao: ComponentDao,
    private val clock: AppClock,
) {

    suspend fun seedIfNeeded() {
        val now = clock.nowEpochMillis()
        val defaults = DefaultComponentCatalog.all(now)
        // Upsert every default so interval/subtype/tag corrections propagate to
        // installs that already have an older catalog. Custom components are untouched.
        dao.upsertAll(defaults.map { it.toEntity() })
        // Drop non-custom rows that are no longer part of the default catalog.
        dao.deleteStaleDefaults(defaults.map { it.id })
    }
}
