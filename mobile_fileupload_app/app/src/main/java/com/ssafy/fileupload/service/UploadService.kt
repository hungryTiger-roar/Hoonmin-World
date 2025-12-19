package com.ssafy.fileupload.service

import com.ssafy.fileupload.dto.UploadResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UploadService {

    @Multipart // 파일 업로드
    @POST("/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part?,
    ):  Response<UploadResult>
}