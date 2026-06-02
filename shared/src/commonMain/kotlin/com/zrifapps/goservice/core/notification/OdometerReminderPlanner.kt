package com.zrifapps.goservice.core.notification

import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Emits a single "update KM" notification per vehicle whose odometer hasn't been
 * touched in [staleDays] days. Fires at [notifyAtHour] local time. Skips vehicles
 * still inside the freshness window.
 *
 * Stable id scheme: `"odo:$vehicleId:$dayBucket"` so re-planning across days produces
 * stable ids per day (idempotent replaceAll).
 */
class OdometerReminderPlanner(
    private val clock: AppClock,
    private val staleDays: Int = DEFAULT_STALE_DAYS,
    private val notifyAtHour: Int = DEFAULT_HOUR,
) {

    fun plan(vehicles: List<Vehicle>): List<OdometerScheduledNotification> {
        val now = clock.nowEpochMillis()
        val tz = TimeZone.currentSystemDefault()
        val staleCutoff = now - staleDays.toLong() * MILLIS_PER_DAY
        val out = mutableListOf<OdometerScheduledNotification>()

        for (vehicle in vehicles) {
            val lastWrite = vehicle.lastOdometerUpdateAt.takeIf { it > 0L } ?: vehicle.createdAt
            val dueAt = lastWrite + staleDays.toLong() * MILLIS_PER_DAY
            // Roll target to notifyAtHour on its local-time date.
            val fireAt = atHourOnDate(dueAt, tz, notifyAtHour)
            val effectiveFire = if (fireAt < now + MIN_LEAD_TIME) {
                // Vehicle is already overdue or due within the next minute — fire next day at notifyAtHour.
                val nextDay = millisToDate(now, tz).plusDays(1)
                nextDay.atTime(notifyAtHour, 0).toInstant(tz).toEpochMilliseconds()
            } else {
                fireAt
            }
            if (lastWrite > staleCutoff && fireAt > now + HORIZON_MILLIS) continue

            val daysSince = ((now - lastWrite) / MILLIS_PER_DAY).coerceAtLeast(0L)
            val title = "Update KM ${vehicle.displayTitle}"
            val body = if (daysSince >= staleDays) {
                "Sudah $daysSince hari sejak terakhir update KM. Pastikan reminder servis tetap akurat."
            } else {
                "Yuk update KM biar reminder servis-nya tetap akurat."
            }
            val dayBucket = (effectiveFire / MILLIS_PER_DAY).toString()
            out += OdometerScheduledNotification(
                id = "odo:${vehicle.id}:$dayBucket",
                vehicleId = vehicle.id,
                title = title,
                body = body,
                triggerAtMillis = effectiveFire,
            )
        }
        return out.sortedBy { it.triggerAtMillis }
    }

    private fun millisToDate(millis: Long, tz: TimeZone): LocalDate =
        Instant.fromEpochMilliseconds(millis).toLocalDateTime(tz).date

    private fun atHourOnDate(millis: Long, tz: TimeZone, hour: Int): Long =
        millisToDate(millis, tz).atTime(hour, 0).toInstant(tz).toEpochMilliseconds()

    private fun LocalDate.plusDays(days: Int): LocalDate = this.plus(days, DateTimeUnit.DAY)

    companion object {
        const val DEFAULT_STALE_DAYS: Int = 14
        const val DEFAULT_HOUR: Int = 9
        const val MILLIS_PER_DAY: Long = 24L * 60L * 60L * 1000L

        /** Don't schedule anything that would fire in the past or within the next minute. */
        const val MIN_LEAD_TIME: Long = 60L * 1000L

        /** Look-ahead window: don't pre-schedule beyond 60 days to keep the work queue small. */
        const val HORIZON_MILLIS: Long = 60L * MILLIS_PER_DAY
    }
}

data class OdometerScheduledNotification(
    val id: String,
    val vehicleId: String,
    val title: String,
    val body: String,
    val triggerAtMillis: Long,
)
