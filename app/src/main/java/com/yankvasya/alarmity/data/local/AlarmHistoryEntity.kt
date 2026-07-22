package com.yankvasya.alarmity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_history")
data class AlarmHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long,
    val timestampMillis: Long,
    val dismissed: Boolean,
)
