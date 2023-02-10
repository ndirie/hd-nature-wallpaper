package com.wallpaper.hdnature.utils.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.utils.getScreenHeight
import com.wallpaper.hdnature.utils.getScreenWidth
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

class CurrentWallpaper {
    var bitmap: Bitmap? = null
    var description: CharSequence? = null
    var icon: Drawable? = null

    constructor(bitmap: Bitmap){
        this.bitmap = bitmap
    }

    constructor(description: CharSequence, icon: Drawable?){
        this.description = description
        this.icon = icon
    }
}

class WallpaperHelper @Inject constructor(@ApplicationContext val context: Context) {
    private val wallpaperManager = WallpaperManager.getInstance(context)

    fun getCurrentWallpaper(): CurrentWallpaper? {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            loadCurrentWallpaper()
        else null
    }

    fun getLockScreenWallpaper(): CurrentWallpaper? {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val descriptor = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK)
            if (descriptor != null) {
                CurrentWallpaper(BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor))
            } else loadCurrentWallpaper()
        } else null
    }

    @RequiresPermission(value = "android.permission.READ_EXTERNAL_STORAGE")
    private fun loadCurrentWallpaper(): CurrentWallpaper {
        if (wallpaperManager.wallpaperInfo != null)
            return CurrentWallpaper(
            try {
                wallpaperManager.wallpaperInfo.loadDescription(context.packageManager)
            } catch (e: Exception){
                "Unknown Error"
            },
            try {
                wallpaperManager.wallpaperInfo.loadThumbnail(context.packageManager)
            } catch (e: Exception){
                ContextCompat.getDrawable(context, R.drawable.ic_gallery_photo_outline)
            }
        )
        return CurrentWallpaper(wallpaperManager.drawable.toBitmap())
    }

    fun Context.applyWallpaper(bitmap: Bitmap, flag: Int? = null, screenCropped: Boolean = false){
        val wallpaperManager = WallpaperManager.getInstance(this)
        val screenHeight = getScreenHeight()
        val screenWidth = getScreenWidth()
        val ratio: Float = screenHeight / bitmap.height.toFloat() // 16/9

        val h = if (bitmap.height > screenHeight) screenHeight else bitmap.height
        val w = if (bitmap.width > screenWidth) screenWidth else bitmap.width

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)

        val thumbBitmap = if (screenCropped) ThumbnailUtils
            .extractThumbnail(scaledBitmap, screenWidth, screenHeight)
        else scaledBitmap

        try {
            if (flag != null)
                wallpaperManager.setBitmap(thumbBitmap, null, true, flag)
            else wallpaperManager.setBitmap(thumbBitmap, null, true)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}