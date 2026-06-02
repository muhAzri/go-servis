package com.zrifapps.goservice.feature.vehicle.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Query("SELECT * FROM vehicles WHERE sync_deleted_at IS NULL ORDER BY created_at ASC")
    fun observeAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE sync_deleted_at IS NULL ORDER BY nickname COLLATE NOCASE ASC")
    fun observeAllAlphabetical(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE sync_deleted_at IS NULL ORDER BY odometer_km ASC")
    fun observeAllOdometerAsc(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE sync_deleted_at IS NULL ORDER BY odometer_km DESC")
    fun observeAllOdometerDesc(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id AND sync_deleted_at IS NULL")
    fun observeOne(id: String): Flow<VehicleEntity?>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getById(id: String): VehicleEntity?

    @Upsert
    suspend fun upsert(entity: VehicleEntity)

    @Query(
        """
        UPDATE vehicles SET
            odometer_km = :odometerKm,
            updated_at = :now,
            last_odometer_update_at = :now,
            sync_local_updated_at = :now,
            sync_status = :syncStatus,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun updateOdometer(id: String, odometerKm: Long, now: Long, syncStatus: String)

    @Query(
        """
        UPDATE vehicles SET
            sync_deleted_at = :now,
            sync_status = :syncStatus,
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: String, now: Long, syncStatus: String)

    @Query("DELETE FROM vehicles WHERE sync_deleted_at IS NOT NULL AND sync_status = 'Synced'")
    suspend fun purgeDeleted()
}
