package com.example.mvp.coapplication.http

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.OkHttpClient


class HttpFactory private constructor() {
    private val retrofit: Retrofit

    companion object {
        @JvmStatic
        val instance: HttpFactory by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpFactory()
        }
    }

    init {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)
        val client = builder.build()
        retrofit = Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    fun getService() : ApiService{
        return retrofit.create(ApiService::class.java)
    }
}