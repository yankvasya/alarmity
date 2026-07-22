package com.yankvasya.alarmity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AlarmEntity::class, AlarmHistoryEntity::class], version = 2, exportSchema = true)
@TypeConverters(RepeatDaysConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun alarmHistoryDao(): AlarmHistoryDao
}
