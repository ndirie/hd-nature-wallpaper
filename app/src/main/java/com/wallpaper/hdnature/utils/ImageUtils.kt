package com.wallpaper.hdnature.utils

import com.wallpaper.hdnature.utils.ext.error
import java.lang.Exception

class ImageRatio() {
    var width: Int = 1
    var height: Int = 1

    constructor(width: Int, height: Int): this() {
        this.width = width
        this.height = height
    }

    constructor(ratio: String): this() {
        try {
            val res = ratio.split("x")
            width = res[0].toInt()
            height = res[1].toInt()
        }catch (e: Exception) {
            error("image_ratio: ", e)
        }
    }

    enum class FormatType {
        IMAGE,
        GIF,
        VIDEO
    }

    data class ImageInfo(
        val imageUrl: String,
        val imageThumb: String,
        val imageName: String = "Unknown",
        val imageAuthor: String = "Unknown",
        var imageTitle: String = "Unknown",
        var imageRatio: ImageRatio = ImageRatio(1,1),
        var type: FormatType = FormatType.IMAGE
    )

}