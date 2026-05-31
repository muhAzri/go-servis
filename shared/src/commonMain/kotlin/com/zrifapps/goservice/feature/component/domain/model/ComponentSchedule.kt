package com.zrifapps.goservice.feature.component.domain.model

import com.zrifapps.goservice.core.value.Distance

/**
 * Pure helpers to derive a tracked component's next-service schedule from its
 * interval (override wins over catalog), an anchor (last service or today), and
 * the current odometer. No platform or storage dependencies so it stays testable.
 */
object ComponentSchedule {

    const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L

    fun resolveIntervalDays(override: Int?, catalog: ComponentInterval?): Int? =
        (override ?: catalog?.durationDays)?.takeIf { it > 0 }

    fun resolveIntervalKm(override: Long?, catalog: ComponentInterval?): Long? =
        (override ?: catalog?.distance?.kilometers)?.takeIf { it > 0 }

    fun nextDate(anchorDate: Long?, intervalDays: Int?): Long? {
        if (anchorDate == null || intervalDays == null) return null
        return anchorDate + intervalDays.toLong() * MILLIS_PER_DAY
    }

    fun nextOdometer(anchorKm: Long?, intervalKm: Long?): Distance? {
        if (anchorKm == null || intervalKm == null) return null
        return Distance.ofKm(anchorKm + intervalKm)
    }

    fun urgency(
        nextDate: Long?,
        nextOdometerKm: Long?,
        currentOdometerKm: Long?,
        nowMillis: Long,
        soonDays: Int = 7,
        soonKm: Long = 500,
    ): ComponentUrgency {
        val byDate = nextDate?.let {
            when {
                it <= nowMillis -> ComponentUrgency.Overdue
                it - nowMillis <= soonDays.toLong() * MILLIS_PER_DAY -> ComponentUrgency.Soon
                else -> ComponentUrgency.Ok
            }
        }
        val byKm = if (nextOdometerKm != null && currentOdometerKm != null) {
            when {
                currentOdometerKm >= nextOdometerKm -> ComponentUrgency.Overdue
                nextOdometerKm - currentOdometerKm <= soonKm -> ComponentUrgency.Soon
                else -> ComponentUrgency.Ok
            }
        } else {
            null
        }
        return listOfNotNull(byDate, byKm).maxByOrNull(::rank) ?: ComponentUrgency.Ok
    }

    private fun rank(urgency: ComponentUrgency): Int = when (urgency) {
        ComponentUrgency.Overdue -> 2
        ComponentUrgency.Soon -> 1
        ComponentUrgency.Ok -> 0
    }
}
