package com.zrifapps.goservice.feature.service.domain.model

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money

data class ServiceRecord(
    val id: String,
    val vehicleId: String,
    val serviceType: ServiceType,
    val serviceDate: Long,
    val odometer: Distance,
    val workshop: String?,
    val cost: Money,
    val note: String?,
    val componentIds: List<String>,
    val sourceReminderId: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val sync: SyncMetadata,
)

data class ServiceRecordDraft(
    val vehicleId: String,
    val serviceType: ServiceType,
    val serviceDate: Long,
    val odometer: Distance,
    val workshop: String?,
    val cost: Money,
    val note: String?,
    val componentIds: List<String>,
    val sourceReminderId: String? = null,
)

data class ServiceFilter(
    val vehicleIds: Set<String> = emptySet(),
    val serviceTypes: Set<ServiceType> = emptySet(),
    val componentIds: Set<String> = emptySet(),
    val dateRange: ClosedRange<Long>? = null,
    val minCost: Money? = null,
    val maxCost: Money? = null,
    val query: String? = null,
)

enum class ServiceSort {
    DateDesc, DateAsc, OdometerDesc, OdometerAsc, CostDesc, CostAsc,
}
