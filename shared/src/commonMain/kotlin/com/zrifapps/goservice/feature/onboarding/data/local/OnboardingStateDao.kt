package com.zrifapps.goservice.feature.onboarding.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OnboardingStateDao {

    @Query("SELECT * FROM onboarding_state WHERE id = :id LIMIT 1")
    fun observe(id: String = OnboardingStateEntity.SINGLETON_ID): Flow<OnboardingStateEntity?>

    @Query("SELECT * FROM onboarding_state WHERE id = :id LIMIT 1")
    suspend fun get(id: String = OnboardingStateEntity.SINGLETON_ID): OnboardingStateEntity?

    @Upsert
    suspend fun upsert(entity: OnboardingStateEntity)

    @Query("DELETE FROM onboarding_state")
    suspend fun clear()
}
