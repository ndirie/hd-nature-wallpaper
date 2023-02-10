package com.wallpaper.hdnature.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wallpaper.hdnature.data.model.category.CategoryModel
import com.wallpaper.hdnature.databinding.CategoryItemBinding
import com.wallpaper.hdnature.databinding.PagerItemBinding
import com.wallpaper.hdnature.utils.ext.loadPhotoUrl
import com.wallpaper.hdnature.utils.ext.toast

class CategoryPagerAdapter(
    private val context: Context,
    private val categoryList: List<CategoryModel>,
    private val listener: (imageView: ImageView, textView: TextView, model: CategoryModel) -> Unit
): RecyclerView.Adapter<CategoryPagerAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(
        val binding: PagerItemBinding,
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryModel) {
            binding.apply {
                categoryImageView.loadPhotoUrl(
                    category.image,
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
                            loadingAnim.isVisible = false
                            return false
                        }
                    }
                )
                categoryTitle.text = category.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            PagerItemBinding.inflate(
                LayoutInflater.from(context),
                parent, false
            )
        )
    }

    override fun getItemCount() = if (categoryList.isNotEmpty()) Int.MAX_VALUE else 0

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val model = categoryList[position % categoryList.size]
        holder.bind(category = model)
        holder.binding.root.setOnClickListener {
            listener.invoke(
                holder.binding.categoryImageView,
                holder.binding.categoryTitle,
                model
            )
        }
    }
}