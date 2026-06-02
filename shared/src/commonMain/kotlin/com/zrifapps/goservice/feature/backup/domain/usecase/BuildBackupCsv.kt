package com.zrifapps.goservice.feature.backup.domain.usecase

import com.zrifapps.goservice.core.time.AppClock
import com.zrifapps.goservice.feature.backup.domain.BackupCsv
import com.zrifapps.goservice.feature.backup.domain.model.BackupCounts
import com.zrifapps.goservice.feature.backup.domain.model.BackupExport
import com.zrifapps.goservice.feature.backup.domain.model.BackupRange
import com.zrifapps.goservice.feature.backup.domain.model.BackupSection
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class BuildBackupCsv(
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: ServiceRepository,
    private val reminderRepository: ReminderRepository,
    private val trackedComponentRepository: TrackedComponentRepository,
    private val clock: AppClock,
) {
    data class Params(
        val sections: Set<BackupSection> = BackupSection.ALL,
        val range: BackupRange = BackupRange.All,
    )

    suspend operator fun invoke(params: Params): BackupExport {
        val now = clock.nowEpochMillis()

        val vehicles = vehicleRepository.observeVehicles().first()
        val cutoff = params.range.days?.let { now - it * MILLIS_PER_DAY }
        val services = serviceRepository.observeRecords().first()
            .let { all -> if (cutoff != null) all.filter { it.serviceDate >= cutoff } else all }
        val reminders = reminderRepository.observe(ReminderFilter(statuses = emptySet())).first()
        val components = trackedComponentRepository.observeAll().first()

        val counts = BackupCounts(
            vehicles = if (BackupSection.Vehicles in params.sections) vehicles.size else 0,
            services = if (BackupSection.Services in params.sections) services.size else 0,
            reminders = if (BackupSection.Reminders in params.sections) reminders.size else 0,
            components = if (BackupSection.Components in params.sections) components.size else 0,
        )

        val content = BackupCsv.build(
            vehicles = vehicles,
            services = services,
            reminders = reminders,
            components = components,
            sections = params.sections,
            exportedAt = now,
        )
        return BackupExport(filename = filenameFor(now), content = content, counts = counts)
    }

    private fun filenameFor(now: Long): String {
        val date = Instant.fromEpochMilliseconds(now)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "servisgo-backup-$date.csv"
    }

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
