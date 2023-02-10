package com.wallpaper.hdnature.utils.ext

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.data.model.user.User
import com.wallpaper.hdnature.utils.BlurHashDecoder
import jp.wasabeef.glide.transformations.BlurTransformation


const val CROSS_FADE_DURATION = 350

fun ImageView.loadPhotoUrl(
    url: String,
    colorInt: Int? = null,
    colorString: String? = null,
    requestListener: RequestListener<Drawable>? = null
) {
    colorInt?.let { background = ColorDrawable(it) }
    colorString?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadPhotoUrlWithThumbnail(
    url: String,
    thumbnailUrl: String,
    color: String?,
    centerCrop: Boolean = false,
    requestListener: RequestListener<Drawable>? = null
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .thumbnail(
            if (centerCrop) {
                Glide.with(context).load(thumbnailUrl).centerCrop()
            } else {
                Glide.with(context).load(thumbnailUrl)
            }
        )
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}
fun ImageView.loadPhotoUrlWithThumbnail(
    url: String,
    thumbnailUrl: String,
    color: String?,
    transformation: Transformation<Bitmap>,
    centerCrop: Boolean = false,
    requestListener: RequestListener<Drawable>? = null
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        .transform(transformation)
        .thumbnail(
            if (centerCrop) {
                Glide.with(context).load(thumbnailUrl).centerCrop()
            } else {
                Glide.with(context).load(thumbnailUrl)
            }
        )
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .into(this)
        .clearOnDetach()
}
fun ImageView.loadPhotoWithBlurHash(
    photo: PhotoModel
) {
    if (photo.width != null && photo.height != null) {
        setImageBitmap(
            BlurHashDecoder.decode(photo.blur_hash, photo.width, photo.height, useCache = false)
        )
    }
}

fun ImageView.loadBlurredImage(
    url: String,
    color: String? = null,
   /* transformation: Transformation<Bitmap>,*/
    requestListener: RequestListener<Drawable>? = null
) {
    color?.let { background = ColorDrawable(Color.parseColor(it)) }
    Glide.with(context)
        .load(url)
        /*.transform(transformation)*/
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .addListener(requestListener)
        .apply(RequestOptions.bitmapTransform(BlurTransformation()))
        .into(this)
        .clearOnDetach()
}

fun ImageView.loadProfilePicture(user: User) {
    loadProfilePicture(user.profile_image?.large)
}

fun ImageView.loadProfilePicture(url: String?) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .transition(DrawableTransitionOptions.withCrossFade(CROSS_FADE_DURATION))
        .into(this)
        .clearOnDetach()
}

/*
fun AspectRatioImageView.setAspectRatio(width: Int?, height: Int?) {
    if (width != null && height != null) {
        aspectRatio = height.toDouble() / width.toDouble()
    }
}*/
