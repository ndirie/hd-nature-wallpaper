package com.wallpaper.hdnature.data.repo

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.wallpaper.hdnature.utils.RotationTransform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class GlideRepo @Inject constructor(@ApplicationContext val context: Context) {

    suspend fun downloadBitmap(
        url: String,
        builder: RequestBuilder<Bitmap>.() -> RequestBuilder<Bitmap>
    ): Bitmap? {
        return suspendCancellableCoroutine { cont ->
            Glide.with(context)
                .asBitmap()
                .load(url)
                .builder()
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        cont.resumeWith(Result.success(null))
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        cont.resumeWith(Result.success(resource))
                        return false
                    }
                }).submit()
        }
    }
}

class GlideRepoImpl @Inject constructor(private val glideRepo: GlideRepo) {
    suspend fun downloadImage(
        url: String,
        rotation: Int = 0
    ): Bitmap? {
        return glideRepo.downloadBitmap(url) {
            transform(RotationTransform(rotateRotationAngle = rotation.toFloat()))
        }
    }
}