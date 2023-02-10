package com.singa.wallpaper.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.databinding.ImageItemBinding
import com.wallpaper.hdnature.utils.ext.toPrettyString

class WallpaperPhotoAdapter(
    private val listener: (wallpaper: PhotoModel, view: View) -> Unit
): PagingDataAdapter<PhotoModel, WallpaperPhotoAdapter.WallpaperViewHolder>
    (PhotoModel.PHOTO_COMPARATOR) {

    class WallpaperViewHolder(val binding: ImageItemBinding): ViewHolder(binding.root) {
        fun bind(wallpaperModel: PhotoModel){
            Glide.with(binding.root)
                .load(wallpaperModel.urls.regular)
                .thumbnail(
                    Glide.with(binding.wallpaperImageView)
                        .load(wallpaperModel.urls.small)
                )
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.loadingAnim.isVisible = false
                        return false
                    }
                })
                .into(binding.wallpaperImageView)
            binding.apply {
                countLikesTv.isVisible = wallpaperModel.likes!! > 0
                countLikesTv.text = wallpaperModel.likes.toPrettyString()
            }
        }
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        getItem(position)?.let { wallpaper ->
            holder.bind(wallpaperModel = wallpaper)
            holder.binding.wallpaperImageView.setOnClickListener {
                listener.invoke(
                    wallpaper, holder.binding.wallpaperImageView
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        return WallpaperViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }
}