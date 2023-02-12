package com.wallpaper.hdnature.utils.ext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.utils.ImageRatio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.Exception
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

const val CROP_WALLPAPER_CODE: Int = 99

fun Context.readFileFromAsset(filename: String): String? {
    return try {
        val inputStream = assets.open(filename)
        val fileSize = inputStream.available()
        val fileBytes = ByteArray(size = fileSize)
        inputStream.read(fileBytes)
        inputStream.close()
        val jsonString = String(bytes = fileBytes)
        jsonString
    }catch (e: IOException){
        null
    }
}

fun Context.readJsonFile(filename: String): String {
    return assets.open(filename)
        .bufferedReader()
        .use { it.readText() }
}

fun Context?.toast(
    text: CharSequence,
    duration: Int = Toast.LENGTH_SHORT){
    this?.let {
        Toast.makeText(it, text, duration).show()
    }
}

fun Context?.toast(
    text: String,
    duration: Int = Toast.LENGTH_SHORT
){
    this?.let {
        Toast.makeText(it, text, duration).show()
    }
}

fun Context?.toast(
    @StringRes textId: Int,
    duration: Int = Toast.LENGTH_SHORT
){
    this?.let {
        Toast.makeText(it, textId, duration).show()
    }
}

fun Context.hasWritePermission(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun Context.hasPermission(vararg permissions: String): Boolean {
    return permissions.all { singlePermission ->
        ContextCompat.checkSelfPermission(this, singlePermission) ==
                PackageManager.PERMISSION_GRANTED
    }
}

fun Context.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent?) -> Unit
): BroadcastReceiver {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onReceive(intent)
        }
    }
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(receiver, intentFilter)
    return receiver
}

fun Context.isInstalledOnExternal() = try {
    (applicationInfo.flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) ==
            ApplicationInfo.FLAG_EXTERNAL_STORAGE
}catch (e: PackageManager.NameNotFoundException){
    false
}

fun Context.openPlayStorePage(){
    val uri: Uri = Uri.parse("market://details?id=$packageName")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
            Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    try {
        startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
    }
}

fun Context.openLink(url:String){
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}
fun Context.shareText(subject: String, content: String){
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, content)
    }
    startActivity(Intent.createChooser(intent, "Share via"))
}

fun Activity.startCropImage(bitmap: Bitmap){
    try {
        @Suppress("DEPRECATION")
        val url = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "", "")
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_ATTACH_DATA)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setDataAndType(uri, "image/*")
            .putExtra("mimeType", "image/*")
        startActivityForResult(Intent.createChooser(intent, "${getString(R.string.app_name)} Set as: "),
            CROP_WALLPAPER_CODE)
    }catch (e: Exception){
        e.printStackTrace()
    }
}

fun Context.isUsingNightMode(): Boolean {
    return when (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}
fun Context.isOrientationLandscape(): Boolean {
    val orientation: Int = resources.configuration.orientation
    return orientation == Configuration.ORIENTATION_LANDSCAPE
}

@SuppressLint("InternalInsetResource")
fun Context.getNavigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else
        0
}

@SuppressLint("InternalInsetResource")
fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else
        0
}


fun Context.isHaveReadExternalStoragePermission() =
    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
    PackageManager.PERMISSION_GRANTED

fun Context.isHaveWriteExternalStoragePermissionForced() =
    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
    PackageManager.PERMISSION_GRANTED

fun Context.isHaveWriteExternalStoragePermission() =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || isHaveWriteExternalStoragePermissionForced()

fun Context.fileExists(filename: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} like ? and " +
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val relativePath = APP_RELATIVE_PATH
        val selectionArgs = arrayOf("%$relativePath", filename)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use {
                return it.count > 0
            } ?: return false
    } else
        return File(APP_LEGACY_PATH, filename).exists()
}

fun Context.getUriForPhoto(fileName: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} like ? and " +
                "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val relativePath = Environment.DIRECTORY_DOWNLOADS
        val selectionArgs = arrayOf("%$relativePath%", fileName)
        val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use {
            return if (it.moveToFirst()) {
                ContentUris.withAppendedId(uri, it.getLong(
                    it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)))
            } else {
                null
            }
        } ?: return null
    } else {
        val file = File(APP_LEGACY_PATH, fileName)
        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, file)
    }
}

enum class WallpaperMode {
    HOME_SCREEN,
    LOCK_SCREEN,
    HOME_LOCK_SCREEN
}

fun Context.setWallpaper(bitmap: Bitmap, rectF: RectF, type: WallpaperMode){
    try {
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels

        val wallpaperManager = WallpaperManager.getInstance(this)
        wallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight)

        if (!wallpaperManager.isWallpaperSupported) {
            toast("Wallpaper isn't supported")
            return
        }

        thread {
            val wallpaperBitmap: Bitmap = Bitmap.createBitmap(
                bitmap,
                (bitmap.width * rectF.left).toInt(),
                (bitmap.height * rectF.top).toInt(),

                (bitmap.width * (rectF.right - rectF.left).absoluteValue).toInt(),
                (bitmap.height * (rectF.bottom - rectF.top).absoluteValue).toInt(),
            )

            when(type) {
                WallpaperMode.HOME_SCREEN -> {
                    wallpaperManager.setBitmap(wallpaperBitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM)
                }

                WallpaperMode.LOCK_SCREEN -> {
                    wallpaperManager.setBitmap(wallpaperBitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK)
                }
                else -> {}
            }
        }.start()

    }catch (e: Exception) {
        error("wallpaper_bitmap", e)
    }
}

fun Context.saveMedia(path: String, format: ImageRatio.FormatType, imageName: ImageRatio.ImageInfo) {
    val resolver = contentResolver

    when(format) {
        ImageRatio.FormatType.IMAGE -> {
            val image = File(path)
            val imageValues = ContentValues()
            imageValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName.imageName)
            imageValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            imageValues.put(MediaStore.Images.Media.TITLE, imageName.imageName)
            imageValues.put(MediaStore.Images.Media.AUTHOR, imageName.imageAuthor)
            imageValues.put(MediaStore.Images.Media.OWNER_PACKAGE_NAME, imageName.imageName)
            imageValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)

            val outFilesDesc = resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageValues
            )

            val outFile = outFilesDesc?.let { resolver.openFileDescriptor(it, "w") }
            val writeOut = FileOutputStream(outFile?.fileDescriptor)
            writeOut.write(image.inputStream().readBytes())
            writeOut.close()
            toast("Image has been saved successfully.")
        }

        ImageRatio.FormatType.GIF -> {

        }
        ImageRatio.FormatType.VIDEO -> {

        }
    }


}
fun Activity.loadMedia(){
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    val mimeType = arrayOf(
        "image/png",
        "image/jpg",
        "image/gif",
        "video/mp4",
        "video/mpeg",
        "image/jpeg",
        "video/webm",
        )

    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
    val i = Intent.createChooser(intent, "Select a wallpaper")
    startActivityForResult(i, 1112)

}

fun Context.sendFeedback(email: String) {

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "message/rfc822"
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    try {
        startActivity(Intent.createChooser(intent, "Choose Email Client..."))
    }catch (e: ActivityNotFoundException) {
        toast("Can't send feedback! please try again.")
    }
}
