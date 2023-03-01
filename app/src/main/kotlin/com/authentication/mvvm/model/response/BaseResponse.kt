package com.authentication.mvvm.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by ThuanPx on 8/8/20.
 */

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "status") val status: Int? = null,
    @Json(name = "serverCode") val serverCode: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") var data: T
)
