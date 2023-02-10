package com.wallpaper.hdnature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wallpaper.hdnature.databinding.LoadStateItemBinding

class WallpaperLoadState(
    private val listener: () -> Unit
): LoadStateAdapter<WallpaperLoadState.WallpaperLoadStateViewHolder>() {

    class WallpaperLoadStateViewHolder(
        val binding: LoadStateItemBinding
    ): ViewHolder(binding.root) {
        fun bind(loadState: LoadState){
            binding.apply {
                errorMsgTv.isVisible = loadState is LoadState.Error
                loadingStateProgress.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: WallpaperLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
        holder.binding.retryButton.setOnClickListener{ listener }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): WallpaperLoadStateViewHolder {
        return WallpaperLoadStateViewHolder(
            LoadStateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }
}