package com.unionware.mes.process.ui.api.module

import com.unionware.mes.process.ui.api.MESProcessApi
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
    fun providePackService(retrofit: Retrofit): MESProcessApi = retrofit.create(MESProcessApi::class.java)
}