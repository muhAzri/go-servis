package com.zrifapps.goservice.feature.reminder.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query(
        """
        SELECT * FROM reminders
        WHERE sync_deleted_at IS NULL
        ORDER BY
            CASE urgency WHEN 'Overdue' THEN 0 WHEN 'Soon' THEN 1 ELSE 2 END,
            target_date ASC
        """
    )
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query(
        """
        SELECT * FROM reminders
        WHERE vehicle_id = :vehicleId AND sync_deleted_at IS NULL
        ORDER BY
            CASE urgency WHEN 'Overdue' THEN 0 WHEN 'Soon' THEN 1 ELSE 2 END,
            target_date ASC
        """
    )
    fun observeForVehicle(vehicleId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id AND sync_deleted_at IS NULL")
    fun observeOne(id: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: String): ReminderEntity?

    @Upsert
    suspend fun upsert(entity: ReminderEntity)

    @Query(
        """
        UPDATE reminders SET
            status = 'Snoozed',
            snoozed_until = :until,
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_status = :syncStatus,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun snooze(id: String, until: Long, now: Long, syncStatus: String)

    @Query(
        """
        UPDATE reminders SET
            status = 'Completed',
            completed_at = :now,
            completed_service_record_id = :serviceRecordId,
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_status = :syncStatus,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun complete(id: String, serviceRecordId: String?, now: Long, syncStatus: String)

    @Query(
        """
        UPDATE reminders SET
            status = 'Dismissed',
            updated_at = :now,
            sync_local_updated_at = :now,
            sync_status = :syncStatus,
            sync_version = sync_version + 1
        WHERE id = :id
        """
    )
    suspend fun dismiss(id: String, now: Long, syncStatus: String)

    @Query(
        """
        UPDATE reminders SET
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
