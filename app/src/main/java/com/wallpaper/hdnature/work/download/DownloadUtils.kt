package com.wallpaper.hdnature.work.download

const val DOWNLOADER_DEFAULT = "default"
const val DOWNLOADER_SYSTEM = "system"

const val ACTION_DOWNLOAD_COMPLETE = "com.wallpaper.hdnature.work.download.ACTION_DOWNLOAD_COMPLETE"

const val DATA_ACTION = "com.wallpaper.hdnature.work.download.DATA_ACTION"
const val DATA_URI = "com.wallpaper.hdnature.work.download.DATA_URI"

const val DOWNLOAD_STATUS = "com.wallpaper.hdnature.work.download.DOWNLOAD_STATUS"

const val STATUS_SUCCESSFUL = 1
const val STATUS_FAILED = 2
const val STATUS_CANCELLED = 3

enum class DownloadAction { DOWNLOAD, WALLPAPER }