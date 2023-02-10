package com.wallpaper.hdnature.data.model.collection

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.wallpaper.hdnature.data.model.user.User
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.model.photo.Tag
import kotlinx.parcelize.Parcelize

@Parcelize
data class CollectionModel(
    val id: String,
    val title: String,
    val description: String?,
    val published_at: String?,
    val updated_at: String?,
    val curated: Boolean?,
    val featured: Boolean?,
    val total_photos: Int,
    val private: Boolean?,
    val share_key: String?,
    val tags: List<Tag>?,
    val cover_photo: PhotoModel?,
    val preview_photos: List<PhotoModel>?,
    val user: User?,
    val links: Links?
) : Parcelable, java.io.Serializable {
    companion object {
        val COLLECTION_COMPARATOR = object : DiffUtil.ItemCallback<CollectionModel>() {
            override fun areItemsTheSame(
                oldItem: CollectionModel,
                newItem: CollectionModel,
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CollectionModel,
                newItem: CollectionModel,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

@Parcelize
data class Links(
    val self: String?,
    val html: String?,
    val photos: String?
) : Parcelable, java.io.Serializable