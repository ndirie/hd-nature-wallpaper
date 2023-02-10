package com.wallpaper.hdnature.ui.photo

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.adapter.TagAdapter
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.model.photo.Tag
import com.wallpaper.hdnature.databinding.ImageInfoLayoutBinding
import com.wallpaper.hdnature.ui.category.CategoryActivity
import com.wallpaper.hdnature.ui.viewmodel.WallpaperViewModel
import com.wallpaper.hdnature.utils.QUERY_EXTRA
import com.wallpaper.hdnature.utils.ext.loadProfilePicture
import com.wallpaper.hdnature.utils.ext.toPrettyString
import com.wallpaper.hdnature.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint

const val WALLPAPER_MODEL = "wall_model"

@AndroidEntryPoint
class WallpaperDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: ImageInfoLayoutBinding
    private var wallpaperModel: PhotoModel? = null

    private lateinit var tagadapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wallpaperModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                it.getParcelable(WALLPAPER_MODEL, PhotoModel::class.java)
            else @Suppress("DEPRECATION")
                it.getParcelable(WALLPAPER_MODEL)
        }
    }

    override fun getTheme(): Int {
        return R.style.Theme_BottomSheetDialog
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = layoutInflater.inflate(R.layout.image_info_layout, null)
        setStyle(style, R.style.Theme_BottomSheetDialog)

        binding = ImageInfoLayoutBinding.bind(view)
        dialog.setContentView(binding.root)
        binding.apply {

            with(userProfile) { loadProfilePicture(wallpaperModel?.user?.profile_image?.small) }
            wallpaperModel?.user?.name.also { photographerNameTv.text = it }
            wallpaperModel?.user?.username.also { "@${it}".also { usernameTv.text = it } }

            likes.text = (wallpaperModel?.likes ?: 0).toPrettyString()
            color.text = (wallpaperModel?.color)

            description.text = wallpaperModel?.alt_description ?: "-"

            tagadapter = TagAdapter { position ->
                startActivity(Intent(requireContext(), CategoryActivity::class.java)
                    .apply {
                        putExtra(QUERY_EXTRA, wallpaperModel?.tags?.get(position)?.title)
                    })
            }.apply {
                submitList(wallpaperModel?.tags)
            }

            tagsRecyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = tagadapter
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }
    companion object {
        @JvmStatic
        fun newInstance(wallpaperModel: PhotoModel): WallpaperDialogFragment {
            return WallpaperDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(WALLPAPER_MODEL, wallpaperModel)
                }
            }
        }
    }

}