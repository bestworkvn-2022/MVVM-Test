package com.authentication.mvvm.feature.login

import com.authentication.mvvm.base.viewmodel.BaseViewModel
import com.authentication.mvvm.data.local.datastore.PreferenceDataStore
import com.authentication.mvvm.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appRepository: AppRepository,
    val preferenceDataStore: PreferenceDataStore
) : BaseViewModel() {
    var isShowLogin = false
    var isShowSignup = false
}