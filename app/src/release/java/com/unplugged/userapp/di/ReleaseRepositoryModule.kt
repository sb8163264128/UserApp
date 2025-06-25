package com.unplugged.userapp.di

import com.unplugged.data.DeviceRepository
import com.unplugged.data.DefaultDeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReleaseRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        realRepository: DefaultDeviceRepositoryImpl
    ): DeviceRepository
}