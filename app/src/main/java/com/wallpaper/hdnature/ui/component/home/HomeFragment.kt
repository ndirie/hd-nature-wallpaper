package com.wallpaper.hdnature.ui.component.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.singa.wallpaper.adapter.WallpaperPhotoAdapter
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.adapter.WallpaperLoadState
import com.wallpaper.hdnature.databinding.FragmentHomeBinding
import com.wallpaper.hdnature.ui.photo.WallpaperActivity
import com.wallpaper.hdnature.ui.viewmodel.PhotosViewModel
import com.wallpaper.hdnature.utils.WALLPAPER_MODEL_EXTRA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: PhotosViewModel by viewModels()
    private lateinit var adapter: WallpaperPhotoAdapter
    private lateinit var layoutManager: GridLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = WallpaperPhotoAdapter { wallpaper, imageView ->
            val intent = Intent(requireContext(), WallpaperActivity::class.java)
            intent.putExtra(WALLPAPER_MODEL_EXTRA, wallpaper)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                imageView,
                "image_transition"
            )
            startActivity(intent, ActivityOptions.makeScaleUpAnimation(
                view, 0, 1, view.width, view.height
            ).toBundle())
        }

        lifecycleScope.launch {
            viewModel.getPhotos("QWfX3f0-rY0")
                .collectLatest {
                    adapter.submitData(it)
                }
        }

        binding.apply {
            swipeLayout.isRefreshing = true

            recyclerview.adapter = adapter
            recyclerview.layoutManager = layoutManager
        }

        adapter.withLoadStateFooter(footer = WallpaperLoadState(adapter::retry))
        adapter.addLoadStateListener { loadStates ->
            binding.loadingLayout.apply {
                errorMsgTv.isVisible = loadStates.refresh is LoadState.Error
                loadingStateProgress.isVisible = loadStates.refresh is LoadState.Loading
                retryButton.isVisible = loadStates.refresh is LoadState.Error
                retryButton.setOnClickListener { adapter.retry() }
            }
        }
        binding.apply {
            recyclerview.adapter = adapter
            recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.apply {
                    swipeLayout.isRefreshing = loadStates.refresh is LoadState.Loading
                    swipeLayout.setOnRefreshListener {
                        adapter.refresh()
                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}