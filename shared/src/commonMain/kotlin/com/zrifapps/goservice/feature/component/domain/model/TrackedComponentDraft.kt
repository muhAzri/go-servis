package com.zrifapps.goservice.feature.component.domain.model

import com.zrifapps.goservice.core.value.Distance

data class TrackedComponentDraft(
    val vehicleId: String,
    val catalogComponentId: String,
    val customName: String? = null,
    val lastServiceDate: Long? = null,
    val lastServiceOdometer: Distance? = null,
    val intervalKmOverride: Long? = null,
    val intervalDaysOverride: Int? = null,
)
