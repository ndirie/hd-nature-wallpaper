package com.wallpaper.hdnature.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.wallpaper.hdnature.data.paging.PhotosPagingSource
import com.wallpaper.hdnature.data.paging.SearchPagingSource
import com.wallpaper.hdnature.data.service.ApiService
import com.wallpaper.hdnature.utils.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PhotosRepo @Inject constructor(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getCollectionsPhotos(id: String) = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PhotosPagingSource(apiService, id)
        }
    ).flow

    fun searchPhotos(query: String) = Pager(
        config = PagingConfig(10),
        pagingSourceFactory = {
            SearchPagingSource(
                apiService = apiService, query
            )
        }
    ).flow

    suspend fun getPhotoDetails(id: String) =
        safeApiCall(dispatcher) { apiService.getPhoto(id) }

}