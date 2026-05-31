package com.zrifapps.goservice.feature.backup.domain.usecase

import com.zrifapps.goservice.feature.backup.domain.BackupCsv
import com.zrifapps.goservice.feature.backup.domain.model.BackupImportResult
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository

class ImportBackupCsv(
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: ServiceRepository,
    private val reminderRepository: ReminderRepository,
    private val trackedComponentRepository: TrackedComponentRepository,
) {
    suspend operator fun invoke(content: String): BackupImportResult {
        val parsed = BackupCsv.parse(content)
        if (!parsed.recognized) return BackupImportResult(malformed = true)

        // Vehicles first so component/service/reminder references resolve.
        val vehicles = vehicleRepository.importMissing(parsed.vehicles)
        val components = trackedComponentRepository.importMissing(parsed.components)
        val services = serviceRepository.importMissing(parsed.services)
        val reminders = reminderRepository.importMissing(parsed.reminders)

        val totalRows = parsed.vehicles.size + parsed.services.size +
            parsed.reminders.size + parsed.components.size
        val imported = vehicles + services + reminders + components

        return BackupImportResult(
            vehicles = vehicles,
            services = services,
            reminders = reminders,
            components = components,
            skipped = totalRows - imported,
            malformed = false,
        )
    }
}
