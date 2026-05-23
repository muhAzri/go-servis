package com.zrifapps.goservice.feature.component.domain.model

import com.zrifapps.goservice.core.value.Distance

data class ComponentInterval(
    val distance: Distance?,
    val durationDays: Int?,
    val displayLabel: String,
) {
    init {
        require(distance != null || durationDays != null || displayLabel.isNotBlank()) {
            "ComponentInterval requires at least one of distance/durationDays/displayLabel"
        }
    }
}
