package com.github.boybeak.pexels.api

import com.github.boybeak.pexels.api.model.Photo
import com.github.boybeak.pexels.api.model.PhotoPage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun searchPhotos(@Query("query") query: String): Call<PhotoPage>
    @GET("curated")
    suspend fun curatedPhotos(): Call<PhotoPage>
    @GET("photos/{id}")
    suspend fun photoInfo(@Path("id") id: Long): Call<Photo>
}