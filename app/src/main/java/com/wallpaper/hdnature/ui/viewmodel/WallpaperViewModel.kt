package com.wallpaper.hdnature.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.repo.GlideRepoImpl
import com.wallpaper.hdnature.data.repo.PhotosRepo
import com.wallpaper.hdnature.utils.Result
import com.wallpaper.hdnature.utils.ext.lazyMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WallpaperViewModel @Inject constructor(
    private val photosRepo: PhotosRepo,
    private val glideRepoImpl: GlideRepoImpl
): ViewModel() {

    /*private val _photoDetailsLiveData: Map<String, LiveData<PhotoModel>> = lazyMap {
        val liveData = MutableLiveData<PhotoModel>()
        viewModelScope.launch {
            when(val result = photosRepo.getPhotoDetails(it)) {
                is Result.Success -> {
                    liveData.postValue(result.value)
                }
                else -> {

                }
            }
        }
        return@lazyMap liveData
    }
*/
    var downloadId: Long? = null
    var downloadUUID: UUID? = null

    /*private suspend fun decodeBitmap(
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
*/

}