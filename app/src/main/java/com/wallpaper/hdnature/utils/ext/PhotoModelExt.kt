package com.wallpaper.hdnature.utils.ext

import com.wallpaper.hdnature.data.model.photo.PhotoModel
import java.util.Locale


const val RAW = "raw"
const val FULL = "full"
const val REGULAR = "regular"
const val SMALL = "small"
const val THUMB = "thumb"

fun getPhotoUrl(photo: PhotoModel, quality: String?): String {
    return when (quality) {
        RAW -> photo.urls.raw
        FULL -> photo.urls.full
        REGULAR -> photo.urls.regular
        SMALL -> photo.urls.small
        THUMB -> photo.urls.thumb
        else -> photo.urls.regular
    }
}

fun getPhotoUrl(photo: PhotoModel): String {
    return photo.urls.full
}

val PhotoModel.fileName: String
    get() = "${this.user.name?.lowercase(Locale.ROOT)?.replace(" ", "-")}-${this.id}.jpg"