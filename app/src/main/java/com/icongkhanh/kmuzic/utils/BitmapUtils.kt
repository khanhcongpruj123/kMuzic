package com.icongkhanh.kmuzic.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

object BitmapUtils {

    fun getBitmapFromMusicFile(path: String): Bitmap? {
        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val bytes = mr.embeddedPicture
        mr.release()
        bytes?.let {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        return null
    }
}
