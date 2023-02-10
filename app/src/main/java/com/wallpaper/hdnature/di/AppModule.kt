package com.wallpaper.hdnature.di

import android.app.WallpaperManager
import android.content.Context
import androidx.work.WorkerParameters
import com.wallpaper.hdnature.data.service.ApiService
import com.wallpaper.hdnature.data.service.DownloadService
import com.wallpaper.hdnature.utils.AppNotificationManager
import com.wallpaper.hdnature.utils.BASE_URL
import com.wallpaper.hdnature.work.download.DownloadManagerWrapper
import com.wallpaper.hdnature.work.download.DownloadWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val CONTENT_TYPE = "Content-Type"
    private const val APPLICATION_JSON = "application/json"
    private const val ACCEPT_VERSION = "Accept-Version"

    @Singleton
    @Provides
    fun createInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        }
    }

    @Singleton
    @Provides
    fun createGsonConvertor(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Singleton
    @Provides
    fun createHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request()
            newRequest.newBuilder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(ACCEPT_VERSION, "v1")
                .build()
            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun createClient(
        httpInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor(interceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(httpInterceptor)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun createRetrofit(
        client: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit
            .Builder()
            .addConverterFactory(gsonConverterFactory)
            .baseUrl(BASE_URL)
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideDownloadManager(@ApplicationContext context: Context): WallpaperManager =
        WallpaperManager.getInstance(context)

    @Singleton
    @Provides
    fun provideDownloadWorker(
        @ApplicationContext context: Context,
        downloadService: DownloadService,
        notificationManager: AppNotificationManager,
        workerParameters: WorkerParameters
    ): DownloadWorker {
        return DownloadWorker(
            context,
            workerParameters,
            downloadService,
            notificationManager
        )
    }

    @Singleton
    @Provides
    fun provideNotificationManager(
        @ApplicationContext
        context: Context
    ): AppNotificationManager{
       return AppNotificationManager(context)
    }

    @Singleton
    @Provides
    fun provideDownloadManagerWrapper (
        @ApplicationContext
        context: Context
    ) = DownloadManagerWrapper(context)


    @Provides
    @Singleton
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO


}