package com.wallpaper.hdnature.utils

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.util.DisplayMetrics
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlin.math.roundToInt

val Int.dp get() = (this / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
val Float.dp get() = this / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

val Int.px get() :Int = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Float.px get() = this * Resources.getSystem().displayMetrics.density


fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun RequestBuilder<Bitmap>.addBitmapListener(
    listener: (isReady: Boolean, resource: Bitmap?, e: GlideException?) -> Unit
): RequestBuilder<Bitmap?> {
    return addListener(object : RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: com.bumptech.glide.request.target.Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            listener(false, null, e)
            return false
        }

        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: com.bumptech.glide.request.target.Target<Bitmap>?,
            dataSource: com.bumptech.glide.load.DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            listener(true, resource, null)
            return false
        }

    })
}

@ColorInt
fun getThemeAttrColor(context: Context, @AttrRes colorAttr: Int): Int {
    val array: TypedArray = context.obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}