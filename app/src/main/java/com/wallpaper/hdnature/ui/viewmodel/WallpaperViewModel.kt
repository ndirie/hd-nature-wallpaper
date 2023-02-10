package com.wallpaper.hdnature.ui.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.repo.GlideRepoImpl
import com.wallpaper.hdnature.data.repo.PhotosRepo
import com.wallpaper.hdnature.utils.Result
import com.wallpaper.hdnature.utils.applyWallpaper
import com.wallpaper.hdnature.utils.ext.error
import com.wallpaper.hdnature.utils.ext.getPhotoUrl
import com.wallpaper.hdnature.utils.ext.lazyMap
import com.wallpaper.hdnature.utils.saveImageToExternal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor(
    private val photosRepo: PhotosRepo,
    private val glideRepoImpl: GlideRepoImpl
): ViewModel() {

    private val _photoDetailsLiveData: Map<String, LiveData<PhotoModel>> = lazyMap {
        val liveData = MutableLiveData<PhotoModel>()
        viewModelScope.launch {
            when(val result = photosRepo.getPhotoDetails(it)) {
                is Result.Success -> {
                    liveData.postValue(result.value)
                }
                else -> {
                    "~~~~~~"
                }
            }
        }
        return@lazyMap liveData
    }

    private val _downloadBitmapLoading = MutableLiveData<Boolean?>()
    val downloadBitmapLoading get() : LiveData<Boolean?> = _downloadBitmapLoading

    private val _wallpaperBitmapLoading = MutableLiveData<Boolean?>()
    val wallpaperLoading get(): LiveData<Boolean?> = _wallpaperBitmapLoading

    fun applyWallpaper(context: Context, photoModel: PhotoModel, flag: Int? = null) {
        _wallpaperBitmapLoading.value = true
        viewModelScope.launch(Dispatchers.IO){
            val bitmap = glideRepoImpl.downloadImage(photoModel.urls.full)
            if (bitmap != null) {
                Log.d("TAG", "applyWallpaper: Done")
                context.applyWallpaper(bitmap, flag)
            } else withContext(Dispatchers.Main) {

            }
        }
    }

    fun downloadWallpaper(context: Context, photoModel: PhotoModel) {
        _downloadBitmapLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = glideRepoImpl.downloadImage(getPhotoUrl(photoModel))
            if (bitmap != null) {
                context.saveImageToExternal(context.getString(
                    R.string.app_name),
                    "wallpaper_${photoModel.id}",
                    bitmap).also {
                    if (it){
                        _downloadBitmapLoading.value = false
                    }
                    _downloadBitmapLoading.value = null
                }
            }
        }
    }


    fun photoDetailsLiveData(id: String) = _photoDetailsLiveData.get(id)

    var downloadId: Long? = null
    var downloadUUID: UUID? = null

    private val _wallpaperBitmap = MutableLiveData<Result<Bitmap>>()
    val wallpaperBitmap: LiveData<Result<Bitmap>> = _wallpaperBitmap

    fun prepareBitmapFromUri(
        contentResolver: ContentResolver,
        uri: Uri
    ) {
        viewModelScope.launch {
            val bitmapResult = decodeBitmap(contentResolver, uri)
            _wallpaperBitmap.postValue(bitmapResult)
        }
    }

    private suspend fun decodeBitmap(
        contentResolver: ContentResolver,
        uri: Uri
    ): Result<Bitmap> {
        return withContext(Dispatchers.IO){
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                }else {
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                Result.Success(bitmap)
            }catch (e: Exception){
                val message = "Error decoding bitmap"
                error(message, e)
                Result.Error(error = message)
            }
        }
    }


}