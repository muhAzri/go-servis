package com.zrifapps.goservice.feature.component.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackedComponentDao {

    @Query(
        """
        SELECT * FROM tracked_components
        WHERE vehicle_id = :vehicleId AND sync_deleted_at IS NULL
        ORDER BY urgency DESC, created_at ASC
        """
    )
    fun observeForVehicle(vehicleId: String): Flow<List<TrackedComponentEntity>>

    @Query(
        """
        SELECT * FROM tracked_components
        WHERE sync_deleted_at IS NULL
        ORDER BY created_at ASC
        """
    )
    fun observeAll(): Flow<List<TrackedComponentEntity>>

    @Query("SELECT * FROM tracked_components WHERE id = :id AND sync_deleted_at IS NULL")
    fun observeOne(id: String): Flow<TrackedComponentEntity?>

    @Query("SELECT * FROM tracked_components WHERE id = :id")
    suspend fun getById(id: String): TrackedComponentEntity?

    @Upsert
    suspend fun upsert(entity: TrackedComponentEntity)

    @Query(
        """
        UPDATE tracked_components SET
            last_service_date = :date,
            last_service_km = :km,
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_status = :syncStatus,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun recordService(
        id: String,
        date: Long,
        km: Long,
        now: Long,
        syncStatus: String,
    )

    @Query(
        """
        UPDATE tracked_components SET
            urgency = :urgency,
            updated_at = :now,
            sync_local_updated_at = :now
        WHERE id = :id
        """
    )
    suspend fun updateUrgency(id: String, urgency: String, now: Long)

    @Query(
        """
        UPDATE tracked_components SET
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
