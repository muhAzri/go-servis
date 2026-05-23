package com.zrifapps.goservice.feature.reminder.domain.model

import com.zrifapps.goservice.core.value.Distance

enum class ReminderTriggerMode { Km, Date, Both }

sealed class ReminderTrigger {
    abstract val mode: ReminderTriggerMode

    data class ByKm(val targetOdometer: Distance) : ReminderTrigger() {
        override val mode: ReminderTriggerMode = ReminderTriggerMode.Km
    }

    data class ByDate(val targetDate: Long) : ReminderTrigger() {
        override val mode: ReminderTriggerMode = ReminderTriggerMode.Date
    }

    data class ByBoth(
        val targetOdometer: Distance,
        val targetDate: Long,
    ) : ReminderTrigger() {
        override val mode: ReminderTriggerMode = ReminderTriggerMode.Both
    }

    val targetKm: Long?
        get() = when (this) {
            is ByKm -> targetOdometer.kilometers
            is ByBoth -> targetOdometer.kilometers
            is ByDate -> null
        }

    val targetDateMillis: Long?
        get() = when (this) {
            is ByDate -> targetDate
            is ByBoth -> targetDate
            is ByKm -> null
        }

    companion object {
        fun fromPersisted(mode: ReminderTriggerMode, targetKm: Long?, targetDate: Long?): ReminderTrigger? =
            when (mode) {
                ReminderTriggerMode.Km -> targetKm?.let { ByKm(Distance.ofKm(it)) }
                ReminderTriggerMode.Date -> targetDate?.let { ByDate(it) }
                ReminderTriggerMode.Both -> if (targetKm != null && targetDate != null) {
                    ByBoth(Distance.ofKm(targetKm), targetDate)
                } else null
            }
    }
}
