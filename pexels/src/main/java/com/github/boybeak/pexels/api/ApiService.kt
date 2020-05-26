package com.github.boybeak.pexels.api

import com.github.boybeak.pexels.api.model.Photo
import com.github.boybeak.pexels.api.model.PhotoPage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    suspend fun searchPhotos(@Query("query") query: String): PhotoPage
    @GET("curated?per_page=48")
    suspend fun curatedPhotos(@Query("page") page: Int = 1): PhotoPage
    @GET("photos/{id}")
    suspend fun photoInfo(@Path("id") id: Long): Photo
}