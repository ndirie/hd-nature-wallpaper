package com.wallpaper.hdnature.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.wallpaper.hdnature.utils.ext.isHaveReadExternalStoragePermission
import com.wallpaper.hdnature.utils.ext.isHaveWriteExternalStoragePermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun ComponentActivity.getPermissionLauncher(
    listener: (isAllPermissionsGrated: Boolean, Map<String, Boolean>) -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        listener(it.values.all { value -> value }, it)
    }
}

fun Fragment.getPermissionLauncher(
    listener: (isAllPermissionsGrated: Boolean, Map<String, Boolean>) -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        listener(it.values.all { value -> value }, it)
    }
}

fun ActivityResultLauncher<Array<String>>.launcherPermission(
    context: Context,
    read: Boolean = false,
    write: Boolean = false,
    extraPermission: Array<String>? = null
){
    val permissionToRequest = mutableListOf<String>()
    if (!context.isHaveWriteExternalStoragePermission() && write)
        permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (!context.isHaveReadExternalStoragePermission() && read)
        permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    extraPermission?.forEach {
        if (it != Manifest.permission.READ_EXTERNAL_STORAGE || it != Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionToRequest.add(it)
    }
    if (permissionToRequest.isNotEmpty())
        launch(permissionToRequest.toTypedArray())
}
enum class ImageFormat {
    JPEG, PNG
}

private fun getImageMimeType(format: ImageFormat) = when (format) {
    ImageFormat.JPEG -> "image/jpeg"
    ImageFormat.PNG -> "image/png"
}
private fun getImageExtension(format: ImageFormat) = when (format) {
    ImageFormat.JPEG -> ".jpg"
    ImageFormat.PNG -> ".png"
}

fun Context.saveImageToExternal(
    appFolder: String,
    imageName: String,
    bitmap: Bitmap,
    format: ImageFormat = ImageFormat.PNG
): Boolean {
    val outStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, getImageMimeType(format))
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/$appFolder"
            )
        }

        try {
            contentResolver.insert(
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                contentValues
            )?.let { uri ->
                contentResolver.openOutputStream(uri)
            } ?: throw IOException("Unable to create Media store entry")
        }catch (e: IOException){
            e.printStackTrace()
            null
        }
    } else {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            null
        else {
            val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/$appFolder")
            imageDir.mkdirs()
            val image = File(imageDir, "$imageName${getImageExtension(format)}")
            FileOutputStream(image)
        }
    }
   return try {
       if (outStream == null) false
       else {
           outStream.use { stream ->
               if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream))
                   throw  IOException("Cant save bitmap")
           }
           true
       }
   }catch (e: IOException){
       e.printStackTrace()
       false
   }

}