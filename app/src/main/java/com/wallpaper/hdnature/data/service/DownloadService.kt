package com.wallpaper.hdnature.data.service

import com.wallpaper.hdnature.utils.API_ACCESS_KEY
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {

    @Streaming
    @GET
    suspend fun downloadFile(
        @Url fileUrl: String
    ): ResponseBody

    @Headers("Authorization: Client-ID $API_ACCESS_KEY")
    @GET("photos/{id}/download")
    suspend fun trackDownload(@Path("id") id: String): ResponseBody

}