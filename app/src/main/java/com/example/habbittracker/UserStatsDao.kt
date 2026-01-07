package com.example.habbittracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 0")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userStats: UserStats)

    @Transaction
    suspend fun addXp(amount: Int) {
        val currentStats = getUserStatsOnce() ?: UserStats()
        val newXp = currentStats.totalXp + amount
        val newLevel = (newXp / 100) + 1
        insertOrUpdate(currentStats.copy(totalXp = newXp, level = newLevel))
    }

    @Query("SELECT * FROM user_stats WHERE id = 0")
    suspend fun getUserStatsOnce(): UserStats?
}
