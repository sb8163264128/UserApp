package com.unplugged.userapp.di

import com.unplugged.data.DeviceRepository
import com.unplugged.data.FakeDeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DebugRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        fakeRepository: FakeDeviceRepositoryImpl
    ): DeviceRepository
}