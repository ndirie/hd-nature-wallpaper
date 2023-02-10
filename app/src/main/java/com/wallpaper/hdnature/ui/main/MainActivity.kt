package com.wallpaper.hdnature.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.adapter.HeaderPagerAdapter
import com.wallpaper.hdnature.databinding.ActivityMainBinding
import com.wallpaper.hdnature.databinding.AppInfoLayoutBinding
import com.wallpaper.hdnature.databinding.FeedbackLayoutBinding
import com.wallpaper.hdnature.ui.about.AboutActivity
import com.wallpaper.hdnature.utils.ext.autoScroll
import com.wallpaper.hdnature.utils.ext.openLink
import com.wallpaper.hdnature.utils.ext.openPlayStorePage
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
                R.id.about_menu ->
                    startActivity(Intent(this, AboutActivity::class.java))
                R.id.rate_menu -> rateApp()
                R.id.privacy_menu -> openPrivacyPolicy()
                R.id.feedback_menu -> setupFeedbackDialog()
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
        try {
            val msg = "Hey, check out ${R.string.app_name} at at: https://play.google.com/store/apps/details?id=com.wallpaper.hdnature"
            val intent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, msg)
                .setType("text/plain")
            startActivity(intent)
        }catch (e: ActivityNotFoundException){
            toast("Can't share app")
            e.printStackTrace()
        }

    }

    private fun rateApp() {
        openPlayStorePage()
    }

    private fun openPrivacyPolicy() {
        val url = "https://www.freeprivacypolicy.com/live/7614fee5-1eb5-4bbe-99ba-0e754ae9b8a4"
        openLink(url)
    }

    private fun setupFeedbackDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.Theme_BottomSheetDialog)
        val alertDialog = alertDialogBuilder.create()

        val view = layoutInflater.inflate(R.layout.feedback_layout, null)
        val viewBinding = FeedbackLayoutBinding.bind(view)
        alertDialog.setView(viewBinding.root)

        viewBinding.apply {
            val message = "{$feedbackTextEv.text}"
            sendFeedbackButton.setOnClickListener {
                if (message.isNotEmpty()) {
                    sendFeedback(msg = message)
                }
            }
        }
    }

    private fun sendFeedback(msg: String, subject: String = "Feedback", email: String = "nourdroidsoft@gmail.com") {
        val intent = Intent(Intent.ACTION_SEND)
            .setType("message/feedback")
            .putExtra(Intent.EXTRA_EMAIL, email)
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, msg)
        try {
            startActivity(Intent.createChooser(intent, "Send feedback.."))
        }catch (e: ActivityNotFoundException) {
            toast("Can't send feedback! please try again.")
        }
    }

}