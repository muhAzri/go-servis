package com.zrifapps.goservice.feature.service.data.local

import com.zrifapps.goservice.core.database.toDomain
import com.zrifapps.goservice.core.database.toEmbed
import com.zrifapps.goservice.core.value.Distance
import com.zrifapps.goservice.core.value.Money
import com.zrifapps.goservice.feature.service.domain.model.ServiceKind
import com.zrifapps.goservice.feature.service.domain.model.ServiceRecord
import com.zrifapps.goservice.feature.service.domain.model.ServiceType

fun ServiceRecordWithComponents.toDomain(): ServiceRecord = ServiceRecord(
    id = record.id,
    vehicleId = record.vehicleId,
    serviceType = ServiceType.fromKey(record.serviceType),
    kind = ServiceKind.fromKey(record.kind),
    customTitle = record.customTitle,
    serviceDate = record.serviceDate,
    odometer = Distance.ofKm(record.odometerKm),
    workshop = record.workshop,
    cost = Money.ofIdr(record.costIdr),
    note = record.note,
    componentIds = componentIds,
    sourceReminderId = record.sourceReminderId,
    createdAt = record.createdAt,
    updatedAt = record.updatedAt,
    sync = record.sync.toDomain(),
)

fun ServiceRecord.toEntity(): ServiceRecordEntity = ServiceRecordEntity(
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
    sourceReminderId = sourceReminderId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    sync = sync.toEmbed(),
)
