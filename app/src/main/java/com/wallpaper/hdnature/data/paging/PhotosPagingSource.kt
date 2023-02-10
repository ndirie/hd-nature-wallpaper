package com.wallpaper.hdnature.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.service.ApiService
import retrofit2.HttpException
import java.io.IOException

class PhotosPagingSource(
    private val apiService: ApiService,
    private val collectionId: String
): PagingSource<Int, PhotoModel>() {
    override fun getRefreshKey(state: PagingState<Int, PhotoModel>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoModel> {
        return try {
            val page = if (params.key == null) 1 else params.key
            val response = apiService.getCollectionsPhotos(collectionId, page!!, params.loadSize)
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.isEmpty()) null else page.plus(1)
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }catch (e: HttpException){
            LoadResult.Error(e)
        }catch (e: IOException){
            LoadResult.Error(e)
        }
    }
}