package com.wallpaper.hdnature.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.data.model.category.CategoryModel
import com.wallpaper.hdnature.databinding.ActivityAboutBinding
import com.wallpaper.hdnature.utils.ext.info
import com.wallpaper.hdnature.utils.ext.sendFeedback

class AboutActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        binding.apply {
            instagramButton.setOnClickListener {
                launch(
                    "http://instagram.com/_u/h_dirie",
                    "com.instagram.android"
                )
            }
            facebookButton.setOnClickListener {
                launch(
                    "https://www.facebook.com/hasdirie/",
                    "com.facebook.katana"
                )
            }

            emailButton.setOnClickListener {
                sendFeedback(
                    email = getString(R.string.app_name)
                )
            }

        }

    }
    private fun setupToolbar(){
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = "About"

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun launch(uriString: String, packageName: String){
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage(packageName)
        try {
            startActivity(intent)
        }catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(uriString)
            ))
        }
    }

}