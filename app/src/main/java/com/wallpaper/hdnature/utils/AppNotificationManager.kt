package com.wallpaper.hdnature.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_NONE
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.wallpaper.hdnature.R
import com.wallpaper.hdnature.ui.photo.WallpaperActivity

class AppNotificationManager(
    private val context: Context
) {

    companion object {
        private const val DOWNLOADS_CHANNEL_ID = "downloads_channel_id"
        const val NEW_AUTO_WALLPAPER_CHANNEL_ID = "new_auto_wallpaper_channel_id"
        private const val NEXT_AUTO_WALLPAPER_CHANNEL_ID = "next_auto_wallpaper_channel_id"

        private val OLD_CHANNEL_IDS = listOf("app_channel_id")

        private const val AUTO_WALLPAPER_TILE_NOTIFICATION_ID = 981
        private const val NEW_AUTO_WALLPAPER_NOTIFICATION_ID = 423
    }

    private val notificationManager by lazy { NotificationManagerCompat.from(context)}

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannels = listOf(
                NotificationChannel(
                    DOWNLOADS_CHANNEL_ID,
                    "Downloads",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
                ).apply { setShowBadge(false) },
                NotificationChannel(
                    NEW_AUTO_WALLPAPER_CHANNEL_ID,
                    "New auto wallpapers",
                    NotificationManager.IMPORTANCE_MIN
                ).apply { setShowBadge(false) }
            )
            notificationManager.createNotificationChannels(notificationChannels)
            notificationManager.notificationChannels
                .filter { it.id in OLD_CHANNEL_IDS }
                .forEach { notificationManager.deleteNotificationChannel(it.id) }
        }
    }

    fun cancelNotifications(id: Int) = notificationManager.cancel(id)

    @SuppressLint("MissingPermission")
    fun showTileServiceDownloadNotification(){
        val builder = Builder(
            context, NEXT_AUTO_WALLPAPER_CHANNEL_ID
        ).apply {
            priority = PRIORITY_DEFAULT
            setSmallIcon(R.drawable.ic_gallery_photo_outline)
            setContentTitle(context.getString(R.string.app_name))
            setProgress(0,0,true)
            setTimeoutAfter(60_000)
        }
        notificationManager.notify(AUTO_WALLPAPER_TILE_NOTIFICATION_ID, builder.build())
    }

    fun hideTileServiceNotification(){
        notificationManager.cancel(AUTO_WALLPAPER_TILE_NOTIFICATION_ID)

    }

    fun getProgressNotificationBuilder(fileName: String, cancelIntent: PendingIntent): Builder {
        return  Builder(context, DOWNLOADS_CHANNEL_ID)
            .apply {
                priority = PRIORITY_LOW
                setSmallIcon(R.drawable.ic_download_arrow)
                setTicker("")
                setOngoing(true)
                setContentTitle(fileName)
                setProgress(0,0,true)
                addAction(0, context.getString(R.string.cancel), cancelIntent)
            }
    }

    fun updateProgressNotification(
        builder: Builder,
        progress: Int
    ) = builder.apply {
        setProgress(100, progress, false)
        if (progress == 100) setSmallIcon(R.drawable.ic_like)
    }

    @SuppressLint("MissingPermission")
    fun showDownloadCompleteNotification(fileName: String, uri: Uri){
        val builder = Builder(context, DOWNLOADS_CHANNEL_ID)
            .apply {
                priority = PRIORITY_LOW
                setSmallIcon(R.drawable.ic_like)
                setContentTitle(fileName)
                setContentText(context.getString(R.string.app_name))
                setContentIntent(getViewPendingIntent(uri))
                setProgress(0,0,false)
                setAutoCancel(true)
            }
        notificationManager.notify(fileName.hashCode(), builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showDownloadErrorNotification(fileName: String){
        val builder = Builder(context, DOWNLOADS_CHANNEL_ID)
            .apply {
                priority = PRIORITY_LOW
                setSmallIcon(R.drawable.ic_like)
                setContentTitle(fileName)
                setContentText(context.getString(R.string.app_name))
                setProgress(0,0,false)
            }
        notificationManager.notify(fileName.hashCode(), builder.build())
    }

    @SuppressLint("MissingPermission")
    fun showNewAutoWallpaperNotification(
        id: String,
        title: String?,
        subtitle: String?,
        prevUrl: String?,
        persist: Boolean
    ){
        val builder = Builder(context, NEW_AUTO_WALLPAPER_CHANNEL_ID)
            .apply {
                priority = PRIORITY_MIN
                setSmallIcon(R.drawable.ic_gallery_photo_outline)
                setContentIntent(getCurrentWallpaperPendingIntent(id))
                setAutoCancel(!persist)
                title?.let { setContentTitle(it) }
                setContentText(subtitle)
                prevUrl?.let {
                    val futureTarget = Glide.with(context)
                        .asBitmap()
                        .load(prevUrl).submit()
                    setLargeIcon(futureTarget.get())
                    Glide.with(context).clear(futureTarget)
                }
                setOngoing(persist)
            }
        notificationManager.notify(NEW_AUTO_WALLPAPER_NOTIFICATION_ID, builder.build())
    }

    fun hideNewAutoWallpaperNotificationEnabled(preferenceValue: Boolean): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = notificationManager.getNotificationChannel(NEW_AUTO_WALLPAPER_CHANNEL_ID)
            channel != null && channel.importance != IMPORTANCE_NONE
        }else {
            preferenceValue
        }
    }

    private fun getCurrentWallpaperPendingIntent(id: String): PendingIntent {
        val intent = Intent(context, WallpaperActivity::class.java)
            .apply {
                putExtra(QUERY_EXTRA, id)
            }
        val flags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    private fun getViewPendingIntent(uri: Uri): PendingIntent {
        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(uri, "image/*")
        }
        val choose = Intent.createChooser(viewIntent, "Open with")
        val flags =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getActivity(context, 0, choose, flags)
    }

}