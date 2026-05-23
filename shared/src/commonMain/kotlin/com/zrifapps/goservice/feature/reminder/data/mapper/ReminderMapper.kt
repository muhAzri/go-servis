package com.zrifapps.goservice.feature.reminder.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.feature.reminder.data.dto.ReminderDto
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency
import com.zrifapps.goservice.feature.service.domain.model.ServiceType

fun ReminderDto.toDomain(): Reminder? {
    val mode = triggerMode.toTriggerMode() ?: return null
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
        status = status.toStatus(),
        urgency = urgency.toUrgency(),
        snoozedUntil = snoozedUntil,
        completedAt = completedAt,
        completedServiceRecordId = completedServiceRecordId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sync = SyncMetadata(
            status = if (deletedAt != null) SyncStatus.PendingDelete else SyncStatus.Synced,
            localUpdatedAt = updatedAt,
            remoteUpdatedAt = updatedAt,
            deletedAt = deletedAt,
            version = version,
        ),
    )
}

fun Reminder.toDto(): ReminderDto = ReminderDto(
    id = id,
    vehicleId = vehicleId,
    serviceType = serviceType.key,
    trackedComponentId = trackedComponentId,
    title = title,
    triggerMode = trigger.mode.name.lowercase(),
    targetKm = trigger.targetKm,
    targetDate = trigger.targetDateMillis,
    note = note,
    notifyDaysBefore = notifyDaysBefore,
    status = status.name.lowercase(),
    urgency = urgency.name.lowercase(),
    snoozedUntil = snoozedUntil,
    completedAt = completedAt,
    completedServiceRecordId = completedServiceRecordId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)

private fun String.toTriggerMode(): ReminderTriggerMode? = when (lowercase()) {
    "km" -> ReminderTriggerMode.Km
    "date" -> ReminderTriggerMode.Date
    "both" -> ReminderTriggerMode.Both
    else -> null
}

private fun String.toStatus(): ReminderStatus = when (lowercase()) {
    "snoozed" -> ReminderStatus.Snoozed
    "completed" -> ReminderStatus.Completed
    "dismissed" -> ReminderStatus.Dismissed
    else -> ReminderStatus.Active
}

private fun String.toUrgency(): ReminderUrgency = when (lowercase()) {
    "overdue" -> ReminderUrgency.Overdue
    "soon" -> ReminderUrgency.Soon
    else -> ReminderUrgency.Ok
}
