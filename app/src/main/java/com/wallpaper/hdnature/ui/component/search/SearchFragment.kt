package com.wallpaper.hdnature.ui.component.search

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.adapter.CategoryAdapter
import com.wallpaper.hdnature.adapter.CategoryPagerAdapter
import com.wallpaper.hdnature.data.model.category.CategoryModel
import com.wallpaper.hdnature.databinding.FragmentSearchBinding
import com.wallpaper.hdnature.ui.category.CategoryActivity
import com.wallpaper.hdnature.ui.photo.WallpaperActivity
import com.wallpaper.hdnature.utils.CATEGORY_MODEL_EXTRA
import com.wallpaper.hdnature.utils.QUERY_EXTRA
import com.wallpaper.hdnature.utils.WALLPAPER_MODEL_EXTRA
import com.wallpaper.hdnature.utils.ext.autoScroll
import com.wallpaper.hdnature.utils.ext.hideKeyboard

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: CategoryAdapter
    private lateinit var pagerAdapter: CategoryPagerAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private var categoryList: List<CategoryModel>? = null

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() {
            return _binding!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = CategoryAdapter(requireContext(),
            CategoryModel.getOtherCategories()) { imageView, textView, model ->
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(CATEGORY_MODEL_EXTRA, model)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                imageView,
                "image_transition"
            )
            startActivity(intent, ActivityOptions.makeScaleUpAnimation(
                view, 0, 1, view.width, view.height
            ).toBundle())
        }
        pagerAdapter = CategoryPagerAdapter(requireContext(),
            CategoryModel.getPagerCategories()) { imageView, textView, model ->
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra(CATEGORY_MODEL_EXTRA, model)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                imageView,
                "image_transition"
            )
            startActivity(intent, ActivityOptions.makeScaleUpAnimation(
                view, 0, 1, view.width, view.height
            ).toBundle())
        }
        binding.apply {
            recyclerview.layoutManager = layoutManager
            recyclerview.adapter = adapter
        }

        binding.apply {
            this.slidePager.adapter = pagerAdapter
            slidePager.offscreenPageLimit = pagerAdapter.itemCount
            slidePager.autoScroll(3000)
        }

        setupSearch()
    }

    private fun setupSearch(){
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                startActivity(Intent(
                    requireContext(),
                    CategoryActivity::class.java).apply {
                        putExtra(QUERY_EXTRA, query) })
                binding.searchBar.hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}