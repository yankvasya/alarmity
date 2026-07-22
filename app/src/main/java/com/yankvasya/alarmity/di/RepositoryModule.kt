package com.yankvasya.alarmity.di

import com.yankvasya.alarmity.data.repository.AlarmHistoryRepository
import com.yankvasya.alarmity.data.repository.AlarmHistoryRepositoryImpl
import com.yankvasya.alarmity.data.repository.AlarmRepository
import com.yankvasya.alarmity.data.repository.AlarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindAlarmHistoryRepository(impl: AlarmHistoryRepositoryImpl): AlarmHistoryRepository
}
