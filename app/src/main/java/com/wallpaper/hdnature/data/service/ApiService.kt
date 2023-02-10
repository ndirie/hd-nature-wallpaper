package com.wallpaper.hdnature.data.service

import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.model.collection.CollectionModel
import com.wallpaper.hdnature.data.model.photo.PhotoResponse
import com.wallpaper.hdnature.data.model.statistcs.PhotoStatistics
import com.wallpaper.hdnature.utils.API_ACCESS_KEY
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Authorization: Client-ID $API_ACCESS_KEY")
    @GET("collections/{id}/photos")
    suspend fun getCollectionsPhotos(
        @Path("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<PhotoModel>

    @Headers("Authorization: Client-ID $API_ACCESS_KEY")
    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): PhotoResponse

    @Headers("Authorization: Client-ID $API_ACCESS_KEY")
    @GET("/collections/{id}/related")
    suspend fun getRelatedCollection(
        @Query("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<PhotoModel>

    @Headers("Authorization: Client-ID $API_ACCESS_KEY")
    @GET("/collections/{id}")
    suspend fun getCollection(
        @Query("id") collectionId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): CollectionModel

    @GET("photos/{id}/statistics")
    suspend fun getPhotosStats(
        @Path("id") id: String,
        @Query("resolution") resolution: String,
        @Query("quantity") quantity: String
    ): PhotoStatistics

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String
    ): PhotoModel

}