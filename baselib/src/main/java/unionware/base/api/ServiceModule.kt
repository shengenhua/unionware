package unionware.base.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import unionware.base.api.basic.BasicApi
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun provideBasicService(retrofit: Retrofit): BasicApi = retrofit.create(BasicApi::class.java)

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideViewService(retrofit: Retrofit): SimulateApi =
        retrofit.create(SimulateApi::class.java)


    @Provides
    @Singleton
    fun provideFlowTaskService(retrofit: Retrofit): FlowTaskApi =
        retrofit.create(FlowTaskApi::class.java)


    @Provides
    @Singleton
    fun provideFileService(retrofit: Retrofit): FileServerApi =
        retrofit.create(FileServerApi::class.java)
}