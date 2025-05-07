package com.unionware.emes.api.module

import com.unionware.emes.api.EMESApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MESServiceModule {
    @Provides
    @Singleton
    fun providePackService(retrofit: Retrofit): EMESApi = retrofit.create(EMESApi::class.java)
}