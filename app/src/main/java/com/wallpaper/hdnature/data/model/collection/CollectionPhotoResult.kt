package com.wallpaper.hdnature.data.model.collection

import com.wallpaper.hdnature.data.model.photo.PhotoModel


data class CollectionPhotoResult(
    val photo: PhotoModel,
    val collection: CollectionModel
)