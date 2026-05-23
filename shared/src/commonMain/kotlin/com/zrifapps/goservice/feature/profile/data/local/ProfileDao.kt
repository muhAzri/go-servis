package com.zrifapps.goservice.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles WHERE sync_deleted_at IS NULL LIMIT 1")
    fun observeCurrent(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE sync_deleted_at IS NULL LIMIT 1")
    suspend fun getCurrent(): ProfileEntity?

    @Upsert
    suspend fun upsert(entity: ProfileEntity)

    @Query("DELETE FROM profiles")
    suspend fun clear()
}
