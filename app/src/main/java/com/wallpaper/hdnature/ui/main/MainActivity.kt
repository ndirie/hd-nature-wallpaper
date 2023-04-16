package com.wallpaper.hdnature.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.ads.mediationtestsuite.MediationTestSuite
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.wallpaper.hdnature.BuildConfig
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.adapter.HeaderPagerAdapter
import com.wallpaper.hdnature.databinding.ActivityMainBinding
import com.wallpaper.hdnature.ui.about.AboutActivity
import com.wallpaper.hdnature.utils.ext.autoScroll
import com.wallpaper.hdnature.utils.ext.openLink
import com.wallpaper.hdnature.utils.ext.openPlayStorePage
import com.wallpaper.hdnature.utils.ext.sendFeedback
import com.wallpaper.hdnature.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHost: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var headerPagerAdapter: HeaderPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
        setupHeaderImages()

        MediationTestSuite.launch(this)
        MobileAds.initialize(this) {}
        val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
    }
    private fun setupNavigation() {
        toolbar = binding.appBar.toolbar
        setSupportActionBar(toolbar)
        drawerLayout = binding.drawerLayout
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.searchFragment)
                Toast.makeText(this@MainActivity, "ok", Toast.LENGTH_SHORT).show()
            false
        }

        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        setupWithNavController(binding.bottomNav, navController)

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.share_menu -> shareApp()
                R.id.about_menu -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.rate_menu -> rateApp()
                R.id.privacy_menu -> openPrivacyPolicy()
                R.id.feedback_menu -> sendFeedback(getString(R.string.my_gmail))
                R.id.more_apps_menu -> moreAppsInPlayStore()
            }
            drawerLayout.close()

            false
        }
    }

    private val headerImages = listOf(
        "https://images.unsplash.com/photo-1549211729-86c3eb921c8a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8OXxra1hLRHBBSGo0WXx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1566985636860-56d10f70bb6e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8Mjd8Y08wYWNsTldHOWN8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1555063200-63bd17843e1d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8Mzh8a2tYS0RwQUhqNFl8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1503848188692-ef8ea7cf65d6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjV8S0NkUnJVMGo3Z3N8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1654849838874-90d13f4300ae?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MjB8X2JWYlA3WmhYNzR8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1672346075461-c925b0dfb900?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8MTR8R3FxdWNnQzBDRGN8fGVufDB8fHx8&auto=format&fit=crop&w=600&q=60",
        "https://images.unsplash.com/photo-1453060590797-2d5f419b54cb?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxjb2xsZWN0aW9uLXBhZ2V8M3xRV2ZYM2YwLXJZMHx8ZW58MHx8fHw%3D&auto=format&fit=crop&w=600&q=60"

    )
    private fun setupHeaderImages(){
        headerPagerAdapter = HeaderPagerAdapter(this, headerImages)
        binding.navView.getHeaderView(0)
            .findViewById<ViewPager2>(R.id.header_pager)
            .apply {
                adapter = headerPagerAdapter
                offscreenPageLimit = headerPagerAdapter.itemCount
                autoScroll(5000)
            }

    }

    private fun shareApp(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hey check out my app (${getString(R.string.app_name)}) at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
            putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    private fun rateApp() {
        openPlayStorePage()
    }

    private fun openPrivacyPolicy() {
        val url = "https://www.freeprivacypolicy.com/live/7614fee5-1eb5-4bbe-99ba-0e754ae9b8a4"
        openLink(url)
    }

    private fun moreAppsInPlayStore() {
        try {
            val url = "market://details?id=$packageName"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

}