package com.zrifapps.goservice.core.value

import kotlin.jvm.JvmInline

@JvmInline
value class Distance(val kilometers: Long) : Comparable<Distance> {
    init {
        require(kilometers >= 0) { "Distance must not be negative" }
    }

    operator fun plus(other: Distance): Distance = Distance(kilometers + other.kilometers)
    operator fun minus(other: Distance): Distance =
        Distance((kilometers - other.kilometers).coerceAtLeast(0))
    override fun compareTo(other: Distance): Int = kilometers.compareTo(other.kilometers)

    companion object {
        val ZERO: Distance = Distance(0L)
        fun ofKm(km: Long): Distance = Distance(km.coerceAtLeast(0))
        fun ofKmOrNull(km: Long?): Distance? = km?.let { ofKm(it) }
    }
}
