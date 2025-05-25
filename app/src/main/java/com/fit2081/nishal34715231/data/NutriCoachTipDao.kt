package com.fit2081.nishal34715231.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriCoachTipDao {
    @Insert
    suspend fun insertTip(tip: NutriCoachTip): Long

    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTipsByUserId(userId: String): Flow<List<NutriCoachTip>>

    @Query("SELECT * FROM nutricoach_tips WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getTipsByUserIdOnce(userId: String): List<NutriCoachTip>

    @Query("DELETE FROM nutricoach_tips WHERE userId = :userId")
    suspend fun deleteAllTipsByUserId(userId: String)
}