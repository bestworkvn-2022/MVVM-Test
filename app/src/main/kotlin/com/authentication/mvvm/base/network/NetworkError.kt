package com.authentication.mvvm.base.network

data class NetworkError(
    val errorCode: String?,
    val message: String?,
    val status: String?
)