package com.zrifapps.goservice.feature.service.data.mapper

import com.zrifapps.goservice.core.sync.SyncMetadata
import com.zrifapps.goservice.core.sync.SyncStatus
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money
import com.zrifapps.goservice.feature.service.data.dto.ServiceRecordDto
import com.zrifapps.goservice.feature.service.domain.model.ServiceKind
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceType

fun ServiceRecordDto.toDomain(): ServiceRecord = ServiceRecord(
    id = id,
    vehicleId = vehicleId,
    serviceType = ServiceType.fromKey(serviceType),
    kind = ServiceKind.fromKey(kind),
    customTitle = customTitle,
    serviceDate = serviceDate,
    odometer = Distance.ofKm(odometerKm),
    workshop = workshop,
    cost = Money.ofIdr(costIdr),
    note = note,
    componentIds = componentIds,
    sourceReminderId = sourceReminderId,
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

fun ServiceRecord.toDto(): ServiceRecordDto = ServiceRecordDto(
    id = id,
    vehicleId = vehicleId,
    serviceType = serviceType.key,
    kind = kind.key,
    customTitle = customTitle,
    serviceDate = serviceDate,
    odometerKm = odometer.kilometers,
    workshop = workshop,
    costIdr = cost.amountIdr,
    note = note,
    componentIds = componentIds,
    sourceReminderId = sourceReminderId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = sync.deletedAt,
    version = sync.version,
)
