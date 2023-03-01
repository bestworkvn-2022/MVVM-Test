package com.authentication.mvvm.data.repository

import com.authentication.mvvm.data.remote.api.ApiService
import com.authentication.mvvm.di.AppDispatchers
import com.authentication.mvvm.di.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Created by ThuanPx on 17/09/2021.
 */

interface AppRepository {
}

class DefaultAppRepository @Inject constructor(
    private val apiService: ApiService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AppRepository {

}
