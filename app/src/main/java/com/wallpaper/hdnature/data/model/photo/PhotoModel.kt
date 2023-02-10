package com.wallpaper.hdnature.data.model.photo

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.wallpaper.hdnature.data.model.collection.CollectionModel
import com.wallpaper.hdnature.data.model.collection.Links
import com.wallpaper.hdnature.data.model.statistcs.PhotoStatistics
import com.wallpaper.hdnature.data.model.user.User
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class PhotoModel(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val width: Int,
    val height: Int,
    val color: String = "#E0E0E0",
    val blur_hash: String,
    val views: Int,
    val downloads: Int,
    val likes: Int,
    var liked_by_user: Boolean,
    val description: String?,
    val alt_description: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tag>?,
    val current_user_collections: @RawValue List<CollectionModel>?,
    val sponsorship: Sponsorship?,
    val urls: Urls,
    val links: Links?,
    val user: User,
    val isFavorite: Boolean,
    val statistics: PhotoStatistics?
) : Parcelable {
    companion object {
        val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoModel>() {
            override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@Parcelize
data class Exif(
    val make: String?,
    val model: String?,
    val exposure_time: String?,
    val aperture: String?,
    val focal_length: String?,
    val iso: Int?,
) : Parcelable

@Parcelize
data class Location(
    val city: String?,
    val country: String?,
    val position: Position?,
) : Parcelable

@Parcelize
data class Position(
    val latitude: Double?,
    val longitude: Double?,
) : Parcelable

@Parcelize
data class Tag(
    val type: String?,
    val title: String?,
) : Parcelable {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Tag>() {
            override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
                return oldItem == newItem
            }

        }
    }
}

@Parcelize
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String,
) : Parcelable

@Parcelize
data class Links(
    val self: String,
    val html: String,
    val download: String,
    val download_location: String,
) : Parcelable

@Parcelize
data class Sponsorship(
    val sponsor: User?,
) : Parcelable

data class PhotoResponse(
    var total: Int,
    var totalPages: Int,
    var results: ArrayList<PhotoModel>
)
