package com.wallpaper.hdnature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wallpaper.hdnature.data.model.photo.Tag
import com.wallpaper.hdnature.databinding.TagItemBinding

class TagAdapter(
    private val clickListener: (position: Int) -> Unit
): ListAdapter<Tag, TagAdapter.TagVewHolder>(Tag.diffUtil) {
    class TagVewHolder(val binding: TagItemBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagVewHolder {
        return TagVewHolder(
            TagItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: TagVewHolder, position: Int) {
        val tag = getItem(position)
        holder.binding.chipTitle.text = tag.title
        holder.binding.chipTitle.setOnClickListener { clickListener.invoke(holder.absoluteAdapterPosition) }
    }
}