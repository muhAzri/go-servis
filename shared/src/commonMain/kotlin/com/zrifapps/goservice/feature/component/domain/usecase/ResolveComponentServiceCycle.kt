package com.zrifapps.goservice.feature.component.domain.usecase

import com.zrifapps.goservice.core.result.DomainResult
import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.feature.component.domain.model.ComponentSchedule
import com.zrifapps.goservice.feature.component.domain.repository.ComponentCatalogRepository
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderDraft
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.reminder.domain.usecase.AddReminder
import com.zrifapps.goservice.feature.reminder.domain.usecase.CompleteReminder
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import kotlinx.coroutines.flow.first

/**
 * After a service is logged for a tracked component: stamp last-service, recompute
 * the next-service schedule, resolve (complete) the component's open reminders, and
 * open a fresh reminder for the next cycle. Best-effort — never fails the caller.
 */
class ResolveComponentServiceCycle(
    private val trackedRepository: TrackedComponentRepository,
    private val catalogRepository: ComponentCatalogRepository,
    private val reminderRepository: ReminderRepository,
    private val completeReminder: CompleteReminder,
    private val addReminder: AddReminder,
    private val clock: AppClock,
) {
    data class Params(
        val trackedComponentId: String,
        val serviceDate: Long,
        val serviceOdometer: Distance,
        val serviceRecordId: String?,
    )

    suspend operator fun invoke(params: Params): DomainResult<Unit> {
        val tracked = trackedRepository.getById(params.trackedComponentId).getOrNull()
            ?: return DomainResult.Success(Unit)

        trackedRepository.recordService(
            id = params.trackedComponentId,
            serviceDate = params.serviceDate,
            serviceOdometer = params.serviceOdometer,
        )

        val catalog = catalogRepository.getById(tracked.catalogComponentId).getOrNull()
        // The component's interval is independent of vehicle type once overrides are
        // applied; fall back to whichever catalog interval is defined.
        val interval = catalog?.intervalMotor ?: catalog?.intervalMobil
        val intervalDays = ComponentSchedule.resolveIntervalDays(tracked.intervalDaysOverride, interval)
        val intervalKm = ComponentSchedule.resolveIntervalKm(tracked.intervalKmOverride, interval)

        val now = clock.nowEpochMillis()
        val nextDate = ComponentSchedule.nextDate(params.serviceDate, intervalDays)
        val nextOdometer = ComponentSchedule.nextOdometer(params.serviceOdometer.kilometers, intervalKm)
        val urgency = ComponentSchedule.urgency(
            nextDate = nextDate,
            nextOdometerKm = nextOdometer?.kilometers,
            currentOdometerKm = params.serviceOdometer.kilometers,
            nowMillis = now,
        )

        val refreshed = trackedRepository.getById(params.trackedComponentId).getOrNull() ?: tracked
        trackedRepository.update(
            refreshed.copy(
                nextServiceDate = nextDate,
                nextServiceOdometer = nextOdometer,
                urgency = urgency,
            ),
        )

        val openReminders = reminderRepository.observeForVehicle(tracked.vehicleId).first()
            .filter {
                it.trackedComponentId == params.trackedComponentId &&
                    (it.status == ReminderStatus.Active || it.status == ReminderStatus.Snoozed)
            }
        openReminders.forEach {
            completeReminder(CompleteReminder.Params(it.id, params.serviceRecordId))
        }

        buildTrigger(nextDate, nextOdometer)?.let { trigger ->
            addReminder(
                ReminderDraft(
                    vehicleId = tracked.vehicleId,
                    serviceType = ServiceType.Other,
                    trackedComponentId = tracked.id,
                    title = tracked.customName ?: catalog?.label ?: "Servis komponen",
                    trigger = trigger,
                    notifyDaysBefore = openReminders.firstOrNull()?.notifyDaysBefore ?: 7,
                ),
            )
        }

        return DomainResult.Success(Unit)
    }

    private fun buildTrigger(nextDate: Long?, nextOdometer: Distance?): ReminderTrigger? = when {
        nextDate != null && nextOdometer != null -> ReminderTrigger.ByBoth(nextOdometer, nextDate)
        nextDate != null -> ReminderTrigger.ByDate(nextDate)
        nextOdometer != null -> ReminderTrigger.ByKm(nextOdometer)
        else -> null
    }
}
