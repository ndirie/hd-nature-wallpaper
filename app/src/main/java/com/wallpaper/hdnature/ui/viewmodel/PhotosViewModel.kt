package com.wallpaper.hdnature.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.repo.PhotosRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repo: PhotosRepo
): ViewModel() {

    fun getPhotos(id: String): Flow<PagingData<PhotoModel>> {
        return repo.getCollectionsPhotos(id).cachedIn(viewModelScope)
    }

    fun searchPhoto(query: String) = repo.searchPhotos(query).cachedIn(viewModelScope)

}