package com.unionware.wms.module

import com.unionware.wms.api.PackingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PackServiceModule {
    @Singleton
    @Provides
    fun providePackService(retrofit: Retrofit): PackingApi {
        return retrofit.create(PackingApi::class.java)
    }
}