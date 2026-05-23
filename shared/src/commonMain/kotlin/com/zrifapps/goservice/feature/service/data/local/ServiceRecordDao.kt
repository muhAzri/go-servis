package com.zrifapps.goservice.feature.service.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceRecordDao {

    @Transaction
    @Query("SELECT * FROM service_records WHERE sync_deleted_at IS NULL ORDER BY service_date DESC")
    fun observeAll(): Flow<List<ServiceRecordWithComponents>>

    @Transaction
    @Query(
        """
        SELECT * FROM service_records
        WHERE vehicle_id = :vehicleId AND sync_deleted_at IS NULL
        ORDER BY service_date DESC
        """
    )
    fun observeForVehicle(vehicleId: String): Flow<List<ServiceRecordWithComponents>>

    @Transaction
    @Query("SELECT * FROM service_records WHERE id = :id AND sync_deleted_at IS NULL")
    fun observeOne(id: String): Flow<ServiceRecordWithComponents?>

    @Transaction
    @Query("SELECT * FROM service_records WHERE id = :id")
    suspend fun getById(id: String): ServiceRecordWithComponents?

    @Upsert
    suspend fun upsertRecord(entity: ServiceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRefs(refs: List<ServiceRecordComponentCrossRef>)

    @Query("DELETE FROM service_record_component_xref WHERE service_record_id = :recordId")
    suspend fun clearCrossRefs(recordId: String)

    @Transaction
    suspend fun upsertWithComponents(entity: ServiceRecordEntity, componentIds: List<String>) {
        upsertRecord(entity)
        clearCrossRefs(entity.id)
        if (componentIds.isNotEmpty()) {
            insertCrossRefs(
                componentIds.map {
                    ServiceRecordComponentCrossRef(serviceRecordId = entity.id, componentId = it)
                }
            )
        }
    }

    @Query(
        """
        UPDATE service_records SET
            sync_deleted_at = :now,
            sync_status = :syncStatus,
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: String, now: Long, syncStatus: String)
}
