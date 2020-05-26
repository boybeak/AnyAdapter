package com.github.boybeak.pexels.api

import android.content.Context
import android.content.pm.PackageManager
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private var apiKey: String = ""
    internal val apiService: ApiService =
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain.request().newBuilder()
                                .addHeader("Authorization", apiKey)
                                .build()
                        )
                    }
                    .build()
            )
            .addConverterFactory(
                GsonConverterFactory.create(
                Gson().newBuilder().create()
            ))
            .build()
            .create(ApiService::class.java)

    fun install(context: Context) {
        if (isInstalled()) {
            return
        }
        val appInfo = context.packageManager.getApplicationInfo(context.packageName,
            PackageManager.GET_META_DATA)
        val metaData = appInfo.metaData

        apiKey = metaData.getString("pexelsApiKey") ?: throw IllegalStateException("No pexels API key found")
    }
    fun isInstalled(): Boolean {
        return apiKey != ""
    }
}

val api: ApiService = Api.apiService