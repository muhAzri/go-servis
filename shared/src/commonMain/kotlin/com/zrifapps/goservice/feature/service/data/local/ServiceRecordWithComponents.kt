package com.zrifapps.goservice.feature.service.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceRecordWithComponents(
    @Embedded val record: ServiceRecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "service_record_id",
        entity = ServiceRecordComponentCrossRef::class,
        projection = ["component_id"],
    )
    val componentIds: List<String>,
)
