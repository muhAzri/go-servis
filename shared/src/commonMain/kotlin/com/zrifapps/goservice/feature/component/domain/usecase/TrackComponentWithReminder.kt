package com.zrifapps.goservice.feature.component.domain.usecase

import com.zrifapps.goservice.core.error.DomainError
import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.usecase.UseCase
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.ComponentSchedule
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponentDraft
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

/**
 * Tracks a component AND, as one user action, derives its next-service schedule
 * from the chosen interval and creates the matching reminder. Drop-in replacement
 * for [TrackComponent] (same use-case signature) so callers don't change shape.
 */
class TrackComponentWithReminder(
    private val trackedRepository: TrackedComponentRepository,
    private val catalogRepository: ComponentCatalogRepository,
    private val vehicleRepository: VehicleRepository,
    private val addReminder: AddReminder,
    private val clock: AppClock,
) : UseCase<TrackedComponentDraft, TrackedComponent> {

    override suspend fun invoke(params: TrackedComponentDraft): DomainResult<TrackedComponent> {
        if (params.vehicleId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("vehicleId"))
        }
        if (params.catalogComponentId.isBlank()) {
            return DomainResult.Failure(DomainError.Validation.FieldRequired("catalogComponentId"))
        }

        val vehicle = vehicleRepository.getById(params.vehicleId).getOrNull()
        val catalog = catalogRepository.getById(params.catalogComponentId).getOrNull()
        val interval = vehicle?.type?.let { catalog?.intervalFor(it) }

        val intervalDays = ComponentSchedule.resolveIntervalDays(params.intervalDaysOverride, interval)
        val intervalKm = ComponentSchedule.resolveIntervalKm(params.intervalKmOverride, interval)

        val now = clock.nowEpochMillis()
        val anchorDate = params.lastServiceDate ?: now
        val anchorKm = params.lastServiceOdometer?.kilometers ?: vehicle?.odometer?.kilometers
        val nextDate = ComponentSchedule.nextDate(anchorDate, intervalDays)
        val nextOdometer = ComponentSchedule.nextOdometer(anchorKm, intervalKm)

        val tracked = when (val result = trackedRepository.track(params)) {
            is DomainResult.Success -> result.data
            is DomainResult.Failure -> return result
        }

        val urgency = ComponentSchedule.urgency(nextDate, nextOdometer?.kilometers, anchorKm, now)
        val withSchedule = tracked.copy(
            nextServiceDate = nextDate,
            nextServiceOdometer = nextOdometer,
            urgency = urgency,
        )
        trackedRepository.update(withSchedule)

        buildTrigger(nextDate, nextOdometer)?.let { trigger ->
            addReminder(
                ReminderDraft(
                    vehicleId = params.vehicleId,
                    serviceType = ServiceType.Other,
                    trackedComponentId = tracked.id,
                    title = params.customName ?: catalog?.label ?: "Servis komponen",
                    trigger = trigger,
                ),
            )
        }

        return DomainResult.Success(withSchedule)
    }

    private fun buildTrigger(nextDate: Long?, nextOdometer: Distance?): ReminderTrigger? = when {
        nextDate != null && nextOdometer != null -> ReminderTrigger.ByBoth(nextOdometer, nextDate)
        nextDate != null -> ReminderTrigger.ByDate(nextDate)
        nextOdometer != null -> ReminderTrigger.ByKm(nextOdometer)
        else -> null
    }
}
