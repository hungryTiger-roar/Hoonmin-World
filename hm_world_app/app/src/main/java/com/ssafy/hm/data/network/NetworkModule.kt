package com.ssafy.hm.data.network

import com.ssafy.hm.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // Retrofit는 baseUrl이 반드시 '/'로 끝나야 한다. 없으면 자동으로 붙여준다.
    private val baseUrl: String = BuildConfig.BASE_URL.let { url ->
        if (url.endsWith("/")) url else "$url/"
    }

    val api: HmApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(HmApi::class.java)
}
