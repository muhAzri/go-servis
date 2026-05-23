package com.zrifapps.goservice.feature.reminder.data.local

import com.zrifapps.goservice.core.database.toDomain
import com.zrifapps.goservice.core.database.toEmbed
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency
import com.zrifapps.goservice.feature.service.domain.model.ServiceType

fun ReminderEntity.toDomain(): Reminder? {
    val mode = runCatching { ReminderTriggerMode.valueOf(triggerMode) }.getOrNull() ?: return null
    val trigger = ReminderTrigger.fromPersisted(mode, targetKm, targetDate) ?: return null
    return Reminder(
        id = id,
        vehicleId = vehicleId,
        serviceType = ServiceType.fromKey(serviceType),
        trackedComponentId = trackedComponentId,
        title = title,
        trigger = trigger,
        note = note,
        notifyDaysBefore = notifyDaysBefore,
        status = runCatching { ReminderStatus.valueOf(status) }.getOrDefault(ReminderStatus.Active),
        urgency = runCatching { ReminderUrgency.valueOf(urgency) }.getOrDefault(ReminderUrgency.Ok),
        snoozedUntil = snoozedUntil,
        completedAt = completedAt,
        completedServiceRecordId = completedServiceRecordId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sync = sync.toDomain(),
    )
}

fun Reminder.toEntity(): ReminderEntity = ReminderEntity(
    id = id,
    vehicleId = vehicleId,
    serviceType = serviceType.key,
    trackedComponentId = trackedComponentId,
    title = title,
    triggerMode = trigger.mode.name,
    targetKm = trigger.targetKm,
    targetDate = trigger.targetDateMillis,
    note = note,
    notifyDaysBefore = notifyDaysBefore,
    status = status.name,
    urgency = urgency.name,
    snoozedUntil = snoozedUntil,
    completedAt = completedAt,
    completedServiceRecordId = completedServiceRecordId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toEmbed(),
)
