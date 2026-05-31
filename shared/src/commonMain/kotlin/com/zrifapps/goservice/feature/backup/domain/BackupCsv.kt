package com.zrifapps.goservice.feature.backup.domain

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.HexColor
import com.zrifapps.goservice.core.value.Money
import com.zrifapps.goservice.feature.backup.domain.model.BackupSection
import com.zrifapps.goservice.feature.component.domain.model.ComponentUrgency
import com.zrifapps.goservice.feature.component.domain.model.TrackedComponent
import com.zrifapps.goservice.feature.reminder.domain.model.Reminder
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderStatus
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTrigger
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderTriggerMode
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderUrgency
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceType
import com.zrifapps.goservice.feature.vehicle.domain.model.Vehicle
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType

/** Parsed payload of a backup file, ready to be merged into the repositories. */
data class BackupParsed(
    val vehicles: List<Vehicle> = emptyList(),
    val services: List<ServiceRecord> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val components: List<TrackedComponent> = emptyList(),
    val recognized: Boolean = false,
)

/**
 * Round-trippable, single-file CSV backup format. Each entity type is a labeled
 * block ("[VEHICLES]" etc.) with a header row then one record per line. Text
 * fields are RFC-4180 quoted; embedded newlines are flattened to keep parsing
 * line-based and robust. IDs are preserved so imports can merge by id.
 */
object BackupCsv {

    private const val MAGIC = "GOSERVICE-BACKUP"
    private const val VERSION = "v1"

    private const val MARK_VEHICLES = "[VEHICLES]"
    private const val MARK_SERVICES = "[SERVICES]"
    private const val MARK_REMINDERS = "[REMINDERS]"
    private const val MARK_COMPONENTS = "[COMPONENTS]"

    private val VEHICLE_HEADER = listOf(
        "id", "ownerProfileId", "nickname", "type", "subtypeId", "brand", "model",
        "year", "plateNumber", "odometerKm", "colorHex", "createdAt", "updatedAt",
    )
    private val SERVICE_HEADER = listOf(
        "id", "vehicleId", "serviceType", "serviceDate", "odometerKm", "workshop",
        "costIdr", "note", "componentIds", "sourceReminderId", "createdAt", "updatedAt",
    )
    private val REMINDER_HEADER = listOf(
        "id", "vehicleId", "serviceType", "trackedComponentId", "title", "triggerMode",
        "targetKm", "targetDate", "note", "notifyDaysBefore", "status", "urgency",
        "snoozedUntil", "completedAt", "completedServiceRecordId", "createdAt", "updatedAt",
    )
    private val COMPONENT_HEADER = listOf(
        "id", "vehicleId", "catalogComponentId", "customName", "lastServiceDate",
        "lastServiceKm", "nextServiceDate", "nextServiceKm", "intervalKmOverride",
        "intervalDaysOverride", "urgency", "createdAt", "updatedAt",
    )

    // ---- Build -----------------------------------------------------------

    fun build(
        vehicles: List<Vehicle>,
        services: List<ServiceRecord>,
        reminders: List<Reminder>,
        components: List<TrackedComponent>,
        sections: Set<BackupSection>,
        exportedAt: Long,
    ): String = buildString {
        append("$MAGIC,$VERSION,$exportedAt\n")
        if (BackupSection.Vehicles in sections) {
            block(MARK_VEHICLES, VEHICLE_HEADER, vehicles, ::vehicleCols)
        }
        if (BackupSection.Services in sections) {
            block(MARK_SERVICES, SERVICE_HEADER, services, ::serviceCols)
        }
        if (BackupSection.Reminders in sections) {
            block(MARK_REMINDERS, REMINDER_HEADER, reminders, ::reminderCols)
        }
        if (BackupSection.Components in sections) {
            block(MARK_COMPONENTS, COMPONENT_HEADER, components, ::componentCols)
        }
    }

    private fun <T> StringBuilder.block(
        marker: String,
        header: List<String>,
        items: List<T>,
        cols: (T) -> List<String>,
    ) {
        append('\n')
        append(marker).append('\n')
        append(row(header)).append('\n')
        items.forEach { append(row(cols(it))).append('\n') }
    }

    private fun vehicleCols(v: Vehicle) = listOf(
        v.id, v.ownerProfileId, v.nickname, v.type.key, v.subtypeId, v.brand, v.model,
        v.year?.toString().orEmpty(), v.plateNumber, v.odometer.kilometers.toString(),
        v.color.value, v.createdAt.toString(), v.updatedAt.toString(),
    )

    private fun serviceCols(s: ServiceRecord) = listOf(
        s.id, s.vehicleId, s.serviceType.key, s.serviceDate.toString(),
        s.odometer.kilometers.toString(), s.workshop.orEmpty(), s.cost.amountIdr.toString(),
        s.note.orEmpty(), s.componentIds.joinToString(";"), s.sourceReminderId.orEmpty(),
        s.createdAt.toString(), s.updatedAt.toString(),
    )

    private fun reminderCols(r: Reminder) = listOf(
        r.id, r.vehicleId, r.serviceType.key, r.trackedComponentId.orEmpty(), r.title,
        r.trigger.mode.name, r.trigger.targetKm?.toString().orEmpty(),
        r.trigger.targetDateMillis?.toString().orEmpty(), r.note.orEmpty(),
        r.notifyDaysBefore.toString(), r.status.name, r.urgency.name,
        r.snoozedUntil?.toString().orEmpty(), r.completedAt?.toString().orEmpty(),
        r.completedServiceRecordId.orEmpty(), r.createdAt.toString(), r.updatedAt.toString(),
    )

    private fun componentCols(c: TrackedComponent) = listOf(
        c.id, c.vehicleId, c.catalogComponentId, c.customName.orEmpty(),
        c.lastServiceDate?.toString().orEmpty(), c.lastServiceOdometer?.kilometers?.toString().orEmpty(),
        c.nextServiceDate?.toString().orEmpty(), c.nextServiceOdometer?.kilometers?.toString().orEmpty(),
        c.intervalKmOverride?.toString().orEmpty(), c.intervalDaysOverride?.toString().orEmpty(),
        c.urgency.name, c.createdAt.toString(), c.updatedAt.toString(),
    )

    private fun row(fields: List<String>): String = fields.joinToString(",") { escape(it) }

    private fun escape(value: String): String {
        val clean = value.replace('\r', ' ').replace('\n', ' ')
        return if (clean.contains(',') || clean.contains('"')) {
            "\"" + clean.replace("\"", "\"\"") + "\""
        } else {
            clean
        }
    }

    // ---- Parse -----------------------------------------------------------

    fun parse(content: String): BackupParsed {
        val lines = content.split('\n').map { it.removeSuffix("\r") }
        if (lines.firstOrNull()?.startsWith(MAGIC) != true) {
            return BackupParsed(recognized = false)
        }
        val vehicles = mutableListOf<Vehicle>()
        val services = mutableListOf<ServiceRecord>()
        val reminders = mutableListOf<Reminder>()
        val components = mutableListOf<TrackedComponent>()

        var section: String? = null
        var expectHeader = false
        for (line in lines) {
            if (line.isBlank()) {
                section = null
                continue
            }
            when (line.trim()) {
                MARK_VEHICLES -> { section = "V"; expectHeader = true; continue }
                MARK_SERVICES -> { section = "S"; expectHeader = true; continue }
                MARK_REMINDERS -> { section = "R"; expectHeader = true; continue }
                MARK_COMPONENTS -> { section = "C"; expectHeader = true; continue }
            }
            if (section == null) continue
            if (expectHeader) { expectHeader = false; continue }
            val cols = splitCsvLine(line)
            when (section) {
                "V" -> parseVehicle(cols)?.let(vehicles::add)
                "S" -> parseService(cols)?.let(services::add)
                "R" -> parseReminder(cols)?.let(reminders::add)
                "C" -> parseComponent(cols)?.let(components::add)
            }
        }
        return BackupParsed(vehicles, services, reminders, components, recognized = true)
    }

    private fun parseVehicle(c: List<String>): Vehicle? {
        val id = c.str(0) ?: return null
        val updatedAt = c.long(12) ?: c.long(11) ?: 0L
        return Vehicle(
            id = id,
            ownerProfileId = c.str(1).orEmpty(),
            nickname = c.str(2).orEmpty(),
            type = VehicleType.fromKey(c.str(3) ?: "motor"),
            subtypeId = c.str(4) ?: "*",
            brand = c.str(5).orEmpty(),
            model = c.str(6).orEmpty(),
            year = c.int(7),
            plateNumber = c.str(8).orEmpty(),
            odometer = Distance.ofKm(c.long(9) ?: 0L),
            color = HexColor(c.str(10) ?: "#2E8B57"),
            createdAt = c.long(11) ?: updatedAt,
            updatedAt = updatedAt,
            sync = SyncMetadata.newLocal(updatedAt),
        )
    }

    private fun parseService(c: List<String>): ServiceRecord? {
        val id = c.str(0) ?: return null
        val updatedAt = c.long(11) ?: c.long(10) ?: 0L
        return ServiceRecord(
            id = id,
            vehicleId = c.str(1).orEmpty(),
            serviceType = ServiceType.fromKey(c.str(2) ?: "other"),
            serviceDate = c.long(3) ?: 0L,
            odometer = Distance.ofKm(c.long(4) ?: 0L),
            workshop = c.str(5),
            cost = Money.ofIdr(c.long(6) ?: 0L),
            note = c.str(7),
            componentIds = c.str(8)?.split(";")?.filter { it.isNotBlank() } ?: emptyList(),
            sourceReminderId = c.str(9),
            createdAt = c.long(10) ?: updatedAt,
            updatedAt = updatedAt,
            sync = SyncMetadata.newLocal(updatedAt),
        )
    }

    private fun parseReminder(c: List<String>): Reminder? {
        val id = c.str(0) ?: return null
        val mode = runCatching { ReminderTriggerMode.valueOf(c.str(5) ?: "") }.getOrNull() ?: return null
        val trigger = ReminderTrigger.fromPersisted(mode, c.long(6), c.long(7)) ?: return null
        val updatedAt = c.long(16) ?: c.long(15) ?: 0L
        return Reminder(
            id = id,
            vehicleId = c.str(1).orEmpty(),
            serviceType = ServiceType.fromKey(c.str(2) ?: "other"),
            trackedComponentId = c.str(3),
            title = c.str(4).orEmpty(),
            trigger = trigger,
            note = c.str(8),
            notifyDaysBefore = c.int(9) ?: 7,
            status = runCatching { ReminderStatus.valueOf(c.str(10) ?: "") }.getOrDefault(ReminderStatus.Active),
            urgency = runCatching { ReminderUrgency.valueOf(c.str(11) ?: "") }.getOrDefault(ReminderUrgency.Ok),
            snoozedUntil = c.long(12),
            completedAt = c.long(13),
            completedServiceRecordId = c.str(14),
            createdAt = c.long(15) ?: updatedAt,
            updatedAt = updatedAt,
            sync = SyncMetadata.newLocal(updatedAt),
        )
    }

    private fun parseComponent(c: List<String>): TrackedComponent? {
        val id = c.str(0) ?: return null
        val updatedAt = c.long(12) ?: c.long(11) ?: 0L
        return TrackedComponent(
            id = id,
            vehicleId = c.str(1).orEmpty(),
            catalogComponentId = c.str(2).orEmpty(),
            customName = c.str(3),
            lastServiceDate = c.long(4),
            lastServiceOdometer = c.long(5)?.let { Distance.ofKm(it) },
            nextServiceDate = c.long(6),
            nextServiceOdometer = c.long(7)?.let { Distance.ofKm(it) },
            intervalKmOverride = c.long(8),
            intervalDaysOverride = c.int(9),
            urgency = runCatching { ComponentUrgency.valueOf(c.str(10) ?: "") }.getOrDefault(ComponentUrgency.Ok),
            createdAt = c.long(11) ?: updatedAt,
            updatedAt = updatedAt,
            sync = SyncMetadata.newLocal(updatedAt),
        )
    }

    private fun List<String>.str(i: Int): String? = getOrNull(i)?.takeIf { it.isNotEmpty() }
    private fun List<String>.long(i: Int): Long? = str(i)?.toLongOrNull()
    private fun List<String>.int(i: Int): Int? = str(i)?.toIntOrNull()

    private fun splitCsvLine(line: String): List<String> {
        val out = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                inQuotes -> when {
                    ch == '"' && i + 1 < line.length && line[i + 1] == '"' -> { sb.append('"'); i++ }
                    ch == '"' -> inQuotes = false
                    else -> sb.append(ch)
                }
                ch == '"' -> inQuotes = true
                ch == ',' -> { out.add(sb.toString()); sb.clear() }
                else -> sb.append(ch)
            }
            i++
        }
        out.add(sb.toString())
        return out
    }
}
