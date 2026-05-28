package com.zrifapps.goservice.feature.component.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components ORDER BY tag, label COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ComponentEntity>>

    @Query("SELECT * FROM components WHERE id = :id")
    suspend fun getById(id: String): ComponentEntity?

    @Upsert
    suspend fun upsert(entity: ComponentEntity)

    @Upsert
    suspend fun upsertAll(entities: List<ComponentEntity>)

    @Query("DELETE FROM components WHERE id = :id AND is_custom = 1")
    suspend fun deleteCustom(id: String): Int

    @Query("DELETE FROM components WHERE is_custom = 0 AND id NOT IN (:keepIds)")
    suspend fun deleteStaleDefaults(keepIds: List<String>): Int
}
