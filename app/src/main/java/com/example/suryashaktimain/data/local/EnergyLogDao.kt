package com.example.suryashaktimain.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EnergyLogEntity)

    @Query("SELECT * FROM energy_logs WHERE userId = :userId ORDER BY date DESC, createdAt DESC")
    fun getLogsForUser(userId: Int): Flow<List<EnergyLogEntity>>

    @Query("SELECT * FROM energy_logs WHERE userId = :userId ORDER BY date DESC, createdAt DESC LIMIT 30")
    fun getLast30LogsForUser(userId: Int): Flow<List<EnergyLogEntity>>

    @Query("DELETE FROM energy_logs WHERE userId = :userId")
    suspend fun clearLogsForUser(userId: Int)
}

