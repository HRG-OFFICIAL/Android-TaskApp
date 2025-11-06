package com.example.data.di

import com.example.data.remote.TaskRemoteDataSource
import com.example.data.remote.TaskRemoteDataSourceImpl
import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.StatisticsRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.StatisticsRepository
import com.example.domain.repository.TaskRepository
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
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindTaskRemoteDataSource(impl: TaskRemoteDataSourceImpl): TaskRemoteDataSource
    
    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(impl: StatisticsRepositoryImpl): StatisticsRepository
}
