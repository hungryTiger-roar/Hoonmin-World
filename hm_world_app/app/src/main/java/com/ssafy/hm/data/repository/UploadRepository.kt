package com.ssafy.hm.data.repository

import com.ssafy.hm.data.network.HmApi
import okhttp3.MultipartBody

class UploadRepository(private val api: HmApi) {
    suspend fun uploadImage(part: MultipartBody.Part): String {
        val response = api.uploadImage(part)
        return response.string()
    }
}
