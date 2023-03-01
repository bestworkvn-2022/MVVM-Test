package com.authentication.mvvm.di

import com.authentication.mvvm.data.remote.api.ApiService
import com.authentication.mvvm.data.repository.AppRepository
import com.authentication.mvvm.data.repository.DefaultAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Created by ThuanPx on 8/7/20.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAppRepository(apiService: ApiService, @Dispatcher(AppDispatchers.IO) ioDispatcher: CoroutineDispatcher): AppRepository {
        return DefaultAppRepository(apiService, ioDispatcher)
    }
}
