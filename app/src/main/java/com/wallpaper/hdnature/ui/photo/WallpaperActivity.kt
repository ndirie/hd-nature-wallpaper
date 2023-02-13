package com.wallpaper.hdnature.ui.photo

import android.Manifest
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.data.model.photo.PhotoModel
import com.wallpaper.hdnature.databinding.ActivityWallpaperBinding
import com.wallpaper.hdnature.databinding.WallpaperTypeLayoutBinding
import com.wallpaper.hdnature.ui.viewmodel.WallpaperViewModel
import com.wallpaper.hdnature.utils.ScreenMeasure
import com.wallpaper.hdnature.utils.WALLPAPER_MODEL_EXTRA
import com.wallpaper.hdnature.utils.applyWallpaper
import com.wallpaper.hdnature.utils.ext.fadeVisibility
import com.wallpaper.hdnature.utils.ext.fileName
import com.wallpaper.hdnature.utils.ext.fitFullScreen
import com.wallpaper.hdnature.utils.ext.getPhotoUrl
import com.wallpaper.hdnature.utils.ext.hasWritePermission
import com.wallpaper.hdnature.utils.ext.hideSystemUI
import com.wallpaper.hdnature.utils.ext.isOrientationLandscape
import com.wallpaper.hdnature.utils.ext.loadBlurredImage
import com.wallpaper.hdnature.utils.ext.loadPhotoUrlWithThumbnail
import com.wallpaper.hdnature.utils.ext.requestPermission
import com.wallpaper.hdnature.utils.ext.setTransparentNavigation
import com.wallpaper.hdnature.utils.ext.setTransparentStatusBar
import com.wallpaper.hdnature.utils.ext.showSystemUI
import com.wallpaper.hdnature.utils.ext.toast
import com.wallpaper.hdnature.utils.playForward
import com.wallpaper.hdnature.utils.playReverse
import com.wallpaper.hdnature.utils.setWallpaper
import com.wallpaper.hdnature.work.download.DownloadManagerWrapper
import com.wallpaper.hdnature.work.download.DownloadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WallpaperActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWallpaperBinding

    private var screenMeasure: ScreenMeasure? = null

    //private val favoriteViewModel: FavoriteViewModel by viewModels()

    private var thumbEven = false
    private var isLastTaskComplete = true
    private var isZoomIn = false
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = { isLastTaskComplete = false }
    private var zoomRunnable = {
        if (isZoomIn) hideSystemUI() else showSystemUI()
    }

    private val viewModel: WallpaperViewModel by viewModels()
    //private var downloadReceiver: BroadcastReceiver? = null

    @Inject
    lateinit var wallpaperManager: WallpaperManager
    @Inject
    lateinit var downloadManagerWrapper: DownloadManagerWrapper


    private var isWallpaperLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWallpaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fitFullScreen()
        setTransparentNavigation()
        setTransparentStatusBar()
        createData()

    }

    private fun createData() {
        val wallpaperModel: PhotoModel?
        when {
            intent.hasExtra(WALLPAPER_MODEL_EXTRA) -> {
                wallpaperModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(WALLPAPER_MODEL_EXTRA, PhotoModel::class.java)!!
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(WALLPAPER_MODEL_EXTRA)!!
                }
                if (wallpaperModel != null) {
                    setupData(wallpaperModel)
                }
            }
            else -> {
                finish()
                toast("Sorry! \nThere was a problem!")
            }
        }
    }
    private fun setupData(photo: PhotoModel) {
        makeActions(photo)
        binding.apply {
            closeBackToggleButton.setOnClickListener {
                if (isZoomIn){
                    wallpaperOptionsContainer.fadeVisibility(View.GONE)
                    wallpaperButton.fadeVisibility(View.VISIBLE)
                    animateZoomOut()
                } else onBackPressedDispatcher.onBackPressed()
            }

            fullImageView.setOnClickListener {
                if (isOrientationLandscape()) return@setOnClickListener
                if (!isZoomIn){
                    animateZoomIn()
                    wallpaperOptionsContainer.fadeVisibility(View.VISIBLE)
                    wallpaperButton.fadeVisibility(View.GONE, 700)
                }else {
                    animateZoomOut()
                    wallpaperOptionsContainer.fadeVisibility(View.GONE)
                    wallpaperButton.fadeVisibility(View.VISIBLE, 700)
                }
            }
        }

        /*val multiplatform = MultiTransformation(
            FastBlurTransform()
        )*/

        binding.apply {
            with(fullImageView) {
                loadPhotoUrlWithThumbnail(
                    url = photo.urls.full,
                    thumbnailUrl = photo.urls.thumb,
                    color = photo.color,
                    centerCrop = true,
                    requestListener = object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            isWallpaperLoaded = true
                            toast("Wallpaper is ready now!")
                            binding.loadingProgressAnim.isVisible = false
                            return false
                        }
                    })

                (if (thumbEven) binding.thumbEvenImage else binding.thumbnailImageView)
                    .loadBlurredImage(
                        photo.urls.thumb,
                        photo.color,
                        /*transformation = multiplatform,*/
                        requestListener = object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean,
                            ): Boolean {
                                if (isLastTaskComplete){
                                    thumbEven = !thumbEven
                                    binding.apply {
                                        thumbEvenImage.fadeVisibility(
                                            if (thumbEven) View.VISIBLE else View.GONE, 500
                                        )

                                        if (thumbnailImageView.visibility != View.VISIBLE)
                                            handler.postDelayed({
                                                thumbnailImageView.fadeVisibility(View.VISIBLE)
                                            },300)
                                    }
                                }
                                val status = isLastTaskComplete
                                isLastTaskComplete = false
                                handler.removeCallbacks(runnable)
                                handler.postDelayed(runnable, 400)
                                return !status
                            }
                        }
                    )

            }
        }

    }
    private fun makeActions(photo: PhotoModel){
        binding.apply {
            wallpaperOptionsLayout.apply {
                infoImageButton.setOnClickListener {
                    WallpaperDialogFragment.newInstance(photo)
                        .show(supportFragmentManager, "")
                }
                downloadImageButton.setOnClickListener {
                    if (!isWallpaperLoaded){
                        toast("Loading.. Please wait")
                        return@setOnClickListener
                    }
                    downloadWallpaper(photo, 1)
                }

                wallpaperButtonSmall.setOnClickListener {
                    if (!isWallpaperLoaded){
                        toast("Loading.. Please wait")
                        return@setOnClickListener
                    }
                    setupWallpaperDialog(photo)
                }

                shareImageButton.setOnClickListener {
                    /*photo.description?.let {
                        val share = Intent.createChooser(Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, photo.links?.html)
                            putExtra(Intent.EXTRA_TITLE, photo.description)
                        }, "Share image:")
                        startActivity(share)
                    }*/
                    setupLoadingDialog(3000, "Share now")
                    lifecycleScope.launch(Dispatchers.Default) {
                        drawableToBitmap(binding.fullImageView.drawable)?.let { bitmap ->
                            bitmapToUri(bitmap, photo)?.let { uri ->
                                shareImage(
                                    uri
                                )
                            }
                        }
                    }

                }
            }

            wallpaperButton.setOnClickListener {
                if (!isWallpaperLoaded){
                    toast("Loading.. Please wait")
                    return@setOnClickListener
                }
                setupWallpaperDialog(photo)
            }
        }
    }
    private fun animateZoomIn() {
        binding.apply {
            closeBackToggleButton.playForward(1.5f)
        }
        createScreenMeasure()
        val interpolatorCompat = PathInterpolatorCompat.create(0.4f, -0.03f, .26f, 1.1f)
        isZoomIn = true
        binding.previewCard.animate()
            .scaleX(screenMeasure!!.scale())
            .scaleY(screenMeasure!!.scale())
            .setInterpolator(interpolatorCompat)
            .setDuration(200)
            .start()
        handler.post(zoomRunnable)
    }

    private fun animateZoomOut(){
        binding.apply {
            closeBackToggleButton.playReverse(1.5f)
        }
        createScreenMeasure()
        isZoomIn = false
        binding.previewCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .start()
        handler.post(zoomRunnable)
    }

    private fun createScreenMeasure(){
        if (screenMeasure == null) {
            screenMeasure = ScreenMeasure(this)
            binding.previewCard.pivotY = screenMeasure!!.pivot()
        }

    }

    private fun setupWallpaperDialog(photo: PhotoModel) {
        val alertDialogBuilder = BottomSheetDialog(this@WallpaperActivity, R.style.Theme_BottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.wallpaper_type_layout, null)
        val viewBinding = WallpaperTypeLayoutBinding.bind(view)
        alertDialogBuilder.setContentView(view)
        viewBinding.apply {
            homeScreenButton.setOnClickListener {
                setupLoadingDialog(5000, "Set wallpaper done successfully")
                setWallpaper(WallpaperFlag.HOME_SCREEN)
                alertDialogBuilder.dismiss()
            }

            lockScreenButton.setOnClickListener {
                setupLoadingDialog(5000, "Set wallpaper done successfully")
                setWallpaper(WallpaperFlag.LOCK_SCREEN)
                alertDialogBuilder.dismiss()
            }

            homeLockScreenButton.setOnClickListener {
                alertDialogBuilder.dismiss()
                setWallpaper(WallpaperFlag.BOTH)
                setupLoadingDialog(5000, "Set wallpaper done successfully")
            }

            useSystemCrop.setOnClickListener {
                //drawableToBitmap(binding.fullImageView.drawable)?.let { it1 -> startCropImage(it1) }
                setupLoadingDialog(2000, null)
                lifecycleScope.launch(Dispatchers.Default) {
                    drawableToBitmap(binding.fullImageView.drawable)?.let { bitmap ->
                        bitmapToUri(bitmap, photo)?.let { uri ->
                            setWallpaper(uri) }
                    }
                }
                alertDialogBuilder.dismiss()
            }
        }
        alertDialogBuilder.show()
    }

    /*private fun handleDownloadIntent(photo: PhotoModel) {
        when {
            intent.hasExtra(DATA_URI) -> when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(DATA_URI, Uri::class.java)?.let {
                        applyWallpaper(it)
                    } ?: @Suppress("DEPRECATION")
                    intent.getParcelableExtra<Uri>(DATA_URI).let {
                        if (it != null) {

                        }
                    }
                }
            }
            else -> toast("no uri")
        }
        try {
            drawableToBitmap(binding.fullImageView.drawable)?.let { bitmap -> bitmapToUri(bitmap, photo = photo)?.let { applyWallpaper(it) } }
        }catch (e: Exception) {
            e.printStackTrace()
        }catch (e: RuntimeException) {
            e.printStackTrace()

        }catch (e: FileNotFoundException) {
            e.printStackTrace()

        }catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
    private fun applyWallpaper(uri: Uri){
        try {
            startActivity(wallpaperManager.getCropAndSetWallpaperIntent(uri))
        }catch (e: IllegalArgumentException){
            observeWallpaperBitmapResult()
            viewModel.prepareBitmapFromUri(contentResolver, uri)
        }
    }
    private fun observeWallpaperBitmapResult() {
        viewModel.wallpaperBitmap.observe(this) {
            if (it is Result.Success) setWallpaperWithBitmap(it.value)
            else toast("Failed to set wallpaper")
        }
    }
    private fun setWallpaperWithBitmap(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.Default){
            try {
                wallpaperManager.setBitmap(bitmap)
            }catch (e: IOException){
                error("Error setting wallpaper bitmap: $bitmap")
            }
        }
    }*/
    private fun downloadPhoto(photo: PhotoModel){
        if (hasWritePermission()){
            val url = getPhotoUrl(photo)
            lifecycleScope.launch {
                downloadManagerWrapper.downloadPhoto(url, photo.fileName)
            }
            toast("Download started")
        } else requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 0)
    }
    private fun downloadWallpaper(photo: PhotoModel, type: Int) {
        if (hasWritePermission())
            if (type == 1) {
                viewModel.downloadId = downloadManagerWrapper.downloadWallpaper(
                    getPhotoUrl(photo),
                    photo.fileName
                )
            } else {
                viewModel.downloadUUID = DownloadWorker.enqueueDownload(
                    this,
                    url = getPhotoUrl(photo),
                    fileName = photo.fileName,
                    photo.id
                )
            }
        else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 0)
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )) {
                AlertDialog.Builder(this, R.style.Theme_BottomSheetDialog)
                    .setTitle("Permission Required")
                    .setMessage("Permission required to save photo.")
                    .setPositiveButton("Accept") { dialog, _ ->
                        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 0)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap, photo: PhotoModel): Uri? = contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, photo.fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
    )?.let { it ->
        contentResolver.openOutputStream(it).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        it
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable is BitmapDrawable)
            return if (drawable.bitmap != null)
                drawable.bitmap
            else null
        var bitmap: Bitmap? = null
        if (drawable?.intrinsicWidth != null)
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        if (bitmap != null) {
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0,0,canvas.width, canvas.height)
            drawable?.draw(canvas)
        }
        return bitmap
    }


    private enum class WallpaperFlag {
        HOME_SCREEN,
        LOCK_SCREEN,
        BOTH
    }

    private fun setWallpaper(flag: WallpaperFlag) {
        /*Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })*/
        lifecycleScope.launch(Dispatchers.IO) {
            when(flag) {
                WallpaperFlag.HOME_SCREEN -> {
                    drawableToBitmap(binding.fullImageView.drawable)?.let { it1 ->
                        applyWallpaper(
                            it1,
                            WallpaperManager.FLAG_SYSTEM,
                            screenCropped = true
                        )
                    }
                    //wallpaperManager.setBitmap(drawableToBitmap(drawable), null, true, WallpaperManager.FLAG_SYSTEM)
                }
                WallpaperFlag.LOCK_SCREEN -> {
                    drawableToBitmap(binding.fullImageView.drawable)?.let { it1 ->
                        applyWallpaper(
                            it1,
                            WallpaperManager.FLAG_LOCK,
                            screenCropped = true
                        )
                    }
                    //wallpaperManager.setBitmap(drawableToBitmap(drawable), null, true, WallpaperManager.FLAG_LOCK)
                }
                WallpaperFlag.BOTH -> {
                    drawableToBitmap(binding.fullImageView.drawable)?.let { it1 ->
                        applyWallpaper(
                            it1, screenCropped = true
                        )
                    }
                    //wallpaperManager.setBitmap(drawableToBitmap(drawable))
                }
            }
        }

    }

    private fun shareImage(uri: Uri): Boolean {
        return try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage = """${shareMessage}http://play.google.com/store/apps/details?id=com.wallpaper.hdnature"""
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
            true
        } catch (e: Exception) {
            e.message?.let { Log.d("shareImage", it) }
            false
        }
    }

    private fun setupLoadingDialog(delay: Long, message: String?) {
        val alertDialog = AlertDialog.Builder(this, R.style.Theme_BottomSheetDialog)
        val progressView = layoutInflater.inflate(R.layout.loading_progress, null)
        alertDialog.setView(progressView)
        alertDialog.create()
        alertDialog.setCancelable(false)
        val alert = alertDialog.create()
        alert.show()
        Handler(Looper.getMainLooper())
            .postDelayed({
                alert.dismiss()
                if (message != null)
                    toast(message)
                         }, delay )
    }

}