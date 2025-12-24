package com.ssafy.hm.ui.state

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun bitmapToMultipart(
    bitmap: Bitmap,
    name: String = "image.jpg"
): MultipartBody.Part {

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos)
    val bytes = bos.toByteArray()

    val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData("file", name, requestBody)
}
