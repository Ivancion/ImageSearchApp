package com.example.imagesearchapp.api

import com.example.imagesearchapp.api.Constants.Companion.CLIENT_ID
import com.example.imagesearchapp.data.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : UnsplashResponse

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("photos")
    suspend fun getListOfPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String
    ) : List<UnsplashPhoto>
}