package com.yankvasya.alarmity.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmHistoryDao {
    @Insert
    suspend fun insert(event: AlarmHistoryEntity)

    @Query("SELECT * FROM alarm_history ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<AlarmHistoryEntity>>
}
