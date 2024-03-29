package com.wallpaper.hdnature.utils

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import java.io.IOException
import kotlin.math.roundToInt

fun Context.applyWallpaper(
    bitmap: Bitmap,
    flag: Int? = null,
    screenCropped: Boolean = false
) {
    val wallpaperManager = WallpaperManager.getInstance(this)
    val screenHeight = getScreenHeight()
    val screenWidth = getScreenWidth()
    val ratio: Float = screenHeight / bitmap.height.toFloat() // 16/9 which is grater then 1
    val h = if (bitmap.height > screenHeight) screenHeight else bitmap.height
    val w = if (bitmap.height > screenHeight) (bitmap.width * ratio).roundToInt() else bitmap.width

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, true)
    val bitmap =
        if (screenCropped) ThumbnailUtils.extractThumbnail(
            scaledBitmap,
            screenWidth,
            screenHeight
        )
        else scaledBitmap
    try {
        if (flag != null) {
            wallpaperManager.setBitmap(bitmap, null, true, flag)
        } else {
            wallpaperManager.setBitmap(bitmap, null, true)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun Context.setWallpaper(uri: Uri) {
    val intent = Intent(Intent.ACTION_ATTACH_DATA)
        .addCategory(Intent.CATEGORY_DEFAULT)
        .setDataAndType(uri, "image/*")
        .putExtra("mimeType", "image/*")
    startActivity(Intent.createChooser(intent, "Set as wallpaper"))
}