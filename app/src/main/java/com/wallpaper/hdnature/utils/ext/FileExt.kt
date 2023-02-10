package com.wallpaper.hdnature.utils.ext

import android.content.Context
import android.os.Environment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wallpaper.hdnature.R
import java.io.File

const val APP_DIRECTORY = "NatureWallpapers"

const val FILE_PROVIDER_AUTHORITY = "com.wallpaper.hdnature.fileprovider"

val APP_RELATIVE_PATH = "${Environment.DIRECTORY_PICTURES}${File.separator}$APP_DIRECTORY"

val APP_LEGACY_PATH = "${Environment
    .getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )}${File.separator}$APP_DIRECTORY"


fun showFileExistsDialog(
    context: Context,
    action: () -> Unit
){
    MaterialAlertDialogBuilder(context)
        .setTitle("Already downloaded")
        .setMessage(context.getString(R.string.exists_file_msg))
        .setPositiveButton("Yes") {_ ,_, -> action.invoke() }
        .setNegativeButton("No", null)
        .show()
}