package com.wallpaper.hdnature.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wallpaper.hdnature.data.model.category.CategoryModel
import com.wallpaper.hdnature.databinding.CategoryItemBinding
import com.wallpaper.hdnature.databinding.HeaderLayoutItemBinding
import com.wallpaper.hdnature.utils.ext.loadPhotoUrl

class HeaderPagerAdapter(
    private val context: Context,
    private val images: List<String>,
): Adapter<HeaderPagerAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(
        val binding: HeaderLayoutItemBinding,
    ): ViewHolder(binding.root) {
        fun bind(url: String) {
            binding.apply {
                image.loadPhotoUrl(
                    url,
                    requestListener = object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            return false
                        }
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(
            HeaderLayoutItemBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun getItemCount() = if (images.isNotEmpty()) Int.MAX_VALUE else 0

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(url = images[position % images.size])
    }
}