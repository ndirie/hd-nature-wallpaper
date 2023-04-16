package com.wallpaper.hdnature.ui.category

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.singa.wallpaper.adapter.WallpaperPhotoAdapter
import com.wallpaper.hdnature.adapter.WallpaperLoadState
import com.wallpaper.hdnature.data.model.category.CategoryModel
import com.wallpaper.hdnature.databinding.ActivityCategoryBinding
import com.wallpaper.hdnature.ui.photo.WallpaperActivity
import com.wallpaper.hdnature.ui.viewmodel.PhotosViewModel
import com.wallpaper.hdnature.utils.CATEGORY_MODEL_EXTRA
import com.wallpaper.hdnature.utils.QUERY_EXTRA
import com.wallpaper.hdnature.utils.WALLPAPER_MODEL_EXTRA
import com.wallpaper.hdnature.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding

    private val viewModel: PhotosViewModel by viewModels()
    private lateinit var adapter: WallpaperPhotoAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var toolbar: Toolbar
    private var categoryModel: CategoryModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = WallpaperPhotoAdapter { wallpaper, view ->
            val intent = Intent(this, WallpaperActivity::class.java)
            intent.putExtra(WALLPAPER_MODEL_EXTRA, wallpaper)
            ActivityOptions.makeSceneTransitionAnimation(
                this,
                view,
                "image_transition"
            )
            startActivity(intent, ActivityOptions.makeScaleUpAnimation(
                view, 0, 1, view.width, view.height
            ).toBundle())
        }

        when {
            intent.hasExtra(CATEGORY_MODEL_EXTRA) -> {
                categoryModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(CATEGORY_MODEL_EXTRA, CategoryModel::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(CATEGORY_MODEL_EXTRA)
                }
                if (categoryModel != null) {
                    setupData(categoryModel!!)
                    setupToolbar(categoryModel!!.title)
                } else {
                    toast("Data is empty")
                    finish()
                }
            }
            intent.hasExtra(QUERY_EXTRA) -> {
                val query: String? = intent.getStringExtra(QUERY_EXTRA)
                if (query != null) {
                    searchWallpapers(query)
                    setupToolbar(query)
                }
                }
            else -> {
                toast("Error")
            }
        }

        MobileAds.initialize(this) {}
        val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

        setupBannerAd()
    }

    private fun setupToolbar(title: String){
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = title

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setupData(model: CategoryModel){
        lifecycleScope.launch {
            viewModel.getPhotos(model.collectionId).collectLatest {
                adapter.submitData(it)
            }
        }
        setupLoading()
        setupAdapter()

    }

    private fun setupAdapter(){
        layoutManager = GridLayoutManager(this, 3)

        binding.apply {
            swipeRefreshLayout.isRefreshing = true
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }
    }

    private fun setupLoading(){
        adapter.withLoadStateFooter(footer = WallpaperLoadState(adapter::retry))
        adapter.addLoadStateListener { loadStates ->
            binding.stateLayout.apply {
                errorMsgTv.isVisible = loadStates.refresh is LoadState.Error
                loadingStateProgress.isVisible = loadStates.refresh is LoadState.Loading
                retryButton.isVisible = loadStates.refresh is LoadState.Error
                retryButton.setOnClickListener { adapter.retry() }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.apply {
                    swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
                    swipeRefreshLayout.setOnRefreshListener {
                        adapter.refresh()
                    }

                }
            }
        }
    }

    private fun searchWallpapers(query: String) {
        lifecycleScope.launch {
            viewModel.searchPhoto(query).collectLatest {
                adapter.submitData(it)
            }
        }
        setupLoading()
        setupAdapter()
    }

    private fun setupBannerAd() {
        val adRequest = AdRequest.Builder().build()
        binding.apply {
            adView.loadAd(adRequest)
        }
    }

}