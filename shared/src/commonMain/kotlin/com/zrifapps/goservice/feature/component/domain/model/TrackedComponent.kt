package com.zrifapps.goservice.feature.component.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.value.Distance

enum class ComponentUrgency { Ok, Soon, Overdue }

data class TrackedComponent(
    val id: String,
    val vehicleId: String,
    val catalogComponentId: String,
    val customName: String? = null,
    val lastServiceDate: Long? = null,
    val lastServiceOdometer: Distance? = null,
    val nextServiceDate: Long? = null,
    val nextServiceOdometer: Distance? = null,
    val intervalKmOverride: Long? = null,
    val intervalDaysOverride: Int? = null,
    val urgency: ComponentUrgency = ComponentUrgency.Ok,
    val createdAt: Long,
    val updatedAt: Long,
    val sync: SyncMetadata,
) {
    fun displayName(catalog: Component?): String = customName ?: catalog?.label ?: catalogComponentId
}
