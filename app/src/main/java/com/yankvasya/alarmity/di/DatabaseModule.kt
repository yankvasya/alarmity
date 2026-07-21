package com.yankvasya.alarmity.di

import android.content.Context
import androidx.room.Room
import com.yankvasya.alarmity.data.local.AlarmDao
import com.yankvasya.alarmity.data.local.AlarmDatabase
import com.yankvasya.alarmity.data.local.AlarmHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext context: Context): AlarmDatabase =
        Room.databaseBuilder(context, AlarmDatabase::class.java, "alarmity.db")
            // Pre-1.0 alpha with no real installed user base yet — not worth writing a migration
            // for every schema change until the app has actual users to preserve data for.
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideAlarmDao(database: AlarmDatabase): AlarmDao = database.alarmDao()

    @Provides
    fun provideAlarmHistoryDao(database: AlarmDatabase): AlarmHistoryDao = database.alarmHistoryDao()
}
