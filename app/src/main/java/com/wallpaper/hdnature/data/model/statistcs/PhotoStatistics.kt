package com.wallpaper.hdnature.data.model.statistcs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Downloads(
    var total: Int,
    var historical: Historical
) : Parcelable

@Parcelize
data class Historical(
    var change: Int,
    var resolution: String,
    var quantity: Int,
    var values: ArrayList<Value>
) : Parcelable

@Parcelize
data class Likes(
    var total: Int,
    var historical: Historical
) : Parcelable

@Parcelize
data class PhotoStatistics(
    var id: String,
    var downloads: Downloads,
    var views: Views,
    var likes: Likes
) : Parcelable

@Parcelize
data class Value(
    var date: String,
    var value: Int,
) : Parcelable

@Parcelize
data class Views(
    var total: Int,
    var historical: Historical?,
) : Parcelable

