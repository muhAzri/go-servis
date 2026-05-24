package com.zrifapps.goservice.feature.reminder.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.feature.service.domain.model.ServiceType

enum class ReminderStatus { Active, Snoozed, Completed, Dismissed }
enum class ReminderUrgency { Ok, Soon, Overdue }

data class Reminder(
    val id: String,
    val vehicleId: String,
    val serviceType: ServiceType,
    val trackedComponentId: String? = null,
    val title: String,
    val trigger: ReminderTrigger,
    val note: String? = null,
    val notifyDaysBefore: Int = 7,
    val status: ReminderStatus = ReminderStatus.Active,
    val urgency: ReminderUrgency = ReminderUrgency.Ok,
    val snoozedUntil: Long? = null,
    val completedAt: Long? = null,
    val completedServiceRecordId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val sync: SyncMetadata,
)

data class ReminderDraft(
    val vehicleId: String,
    val serviceType: ServiceType,
    val trackedComponentId: String? = null,
    val title: String,
    val trigger: ReminderTrigger,
    val note: String? = null,
    val notifyDaysBefore: Int = 7,
)

data class ReminderFilter(
    val vehicleIds: Set<String> = emptySet(),
    val statuses: Set<ReminderStatus> = setOf(ReminderStatus.Active, ReminderStatus.Snoozed),
    val urgencies: Set<ReminderUrgency> = emptySet(),
    val serviceTypes: Set<ServiceType> = emptySet(),
    val query: String? = null,
)

enum class ReminderSort {
    UrgencyDesc, DueSoonAsc, CreatedDesc,
}
