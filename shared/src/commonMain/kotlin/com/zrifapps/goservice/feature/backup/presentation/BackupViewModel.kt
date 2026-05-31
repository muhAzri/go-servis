package com.zrifapps.goservice.feature.backup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zrifapps.goservice.core.presentation.Cancellable
import com.zrifapps.goservice.core.presentation.subscribeOn
import com.zrifapps.goservice.feature.backup.domain.model.BackupCounts
import com.zrifapps.goservice.feature.backup.domain.model.BackupExport
import com.zrifapps.goservice.feature.backup.domain.model.BackupImportResult
import com.zrifapps.goservice.feature.backup.domain.model.BackupRange
import com.zrifapps.goservice.feature.backup.domain.model.BackupSection
import com.zrifapps.goservice.feature.backup.domain.usecase.BuildBackupCsv
import com.zrifapps.goservice.feature.backup.domain.usecase.ImportBackupCsv
import com.zrifapps.goservice.feature.component.domain.repository.TrackedComponentRepository
import com.zrifapps.goservice.feature.reminder.domain.model.ReminderFilter
import com.zrifapps.goservice.feature.reminder.domain.repository.ReminderRepository
import com.zrifapps.goservice.feature.service.domain.repository.ServiceRepository
import com.zrifapps.goservice.feature.vehicle.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BackupViewModel(
    private val buildBackupCsv: BuildBackupCsv,
    private val importBackupCsv: ImportBackupCsv,
    vehicleRepository: VehicleRepository,
    serviceRepository: ServiceRepository,
    reminderRepository: ReminderRepository,
    trackedComponentRepository: TrackedComponentRepository,
) : ViewModel() {

    data class UiState(
        val counts: BackupCounts = BackupCounts(),
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                vehicleRepository.observeVehicles(),
                serviceRepository.observeRecords(),
                reminderRepository.observe(ReminderFilter(statuses = emptySet())),
                trackedComponentRepository.observeAll(),
            ) { vehicles, services, reminders, components ->
                BackupCounts(
                    vehicles = vehicles.size,
                    services = services.size,
                    reminders = reminders.size,
                    components = components.size,
                )
            }.collect { counts -> _state.update { it.copy(counts = counts) } }
        }
    }

    /** Builds the CSV; the caller writes it to a file and opens the OS share sheet. */
    suspend fun export(sectionKeys: Set<String>, rangeKey: String): BackupExport {
        val sections = sectionKeys.mapNotNull { BackupSection.fromKey(it) }.toSet()
            .ifEmpty { BackupSection.ALL }
        return buildBackupCsv(BuildBackupCsv.Params(sections, BackupRange.fromKey(rangeKey)))
    }

    /** Parses and merges a previously exported CSV (skips ids that already exist). */
    suspend fun importCsv(content: String): BackupImportResult = importBackupCsv(content)

    fun observeState(onChange: (UiState) -> Unit): Cancellable =
        state.subscribeOn(viewModelScope, onChange)
}
